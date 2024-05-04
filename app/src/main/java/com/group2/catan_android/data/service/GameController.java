package com.group2.catan_android.data.service;

import android.graphics.Paint;

import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.api.JoinGameResponse;
import com.group2.catan_android.data.live.PlayersInLobbyDto;
import com.group2.catan_android.data.repository.lobby.LobbyJoiner;
import com.group2.catan_android.data.repository.lobby.LobbyRepository;
import com.group2.catan_android.data.repository.player.PlayerRepository;
import com.group2.catan_android.data.repository.token.TokenRepository;
import com.group2.catan_android.networking.dto.Game;
import com.group2.catan_android.networking.socket.SocketManager;

import java.util.function.Function;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;

/**
 * GameController manages everything related to a game. It coordinates all app components.
 * it bootstraps all datasources
 */
public class GameController implements GameJoiner, GameLeaver{
    private static GameController instance;
    private final StompManager stompManager;
    private final TokenRepository tokenRepository;
    private final PlayerRepository playerRepository;
    private final LobbyJoiner lobbyJoiner;

    private GameController(StompManager stompManager, TokenRepository tokenRepository, PlayerRepository playerRepository, LobbyJoiner lobbyJoiner){
        this.stompManager = stompManager;
        this.tokenRepository = tokenRepository;
        this.playerRepository = playerRepository;
        this.lobbyJoiner = lobbyJoiner;
    }
    public static GameController getInstance(){
        return instance;
    }
    public static void initialize(StompManager stompManager, TokenRepository tokenRepository, PlayerRepository playerRepository, LobbyJoiner lobbyJoiner){
        instance = new GameController(stompManager, tokenRepository, playerRepository, lobbyJoiner);
    }
    public Completable joinGame(JoinGameRequest request){
        return processGameRequest(request, lobbyJoiner::joinGame);
    }

    public Completable createGame(JoinGameRequest request){
        return processGameRequest(request, lobbyJoiner::createGame);
    }

    public Completable leaveGame(){
        String token = tokenRepository.getToken();
        if(token == null)
            return Completable.error(new IllegalStateException("No Token in Repository"));
        return lobbyJoiner.leaveGame(token)
                .andThen(Completable.fromAction(stompManager::shutdown))
                .subscribeOn(Schedulers.io());
    }
    private Completable processGameRequest(JoinGameRequest request, Function<JoinGameRequest, Single<JoinGameResponse>> gameAction){
        return Single.defer(() -> gameAction.apply(request))
                .flatMapCompletable(this::processJoinGameResponse)
                .subscribeOn(Schedulers.io());
    }
    private Completable processJoinGameResponse(JoinGameResponse joinGameResponse){
        return stompManager.connect(joinGameResponse.getToken())
                .andThen(Completable.fromAction(() -> {
                    initStompListeners(joinGameResponse);
                    wireUpLiveDataSources(joinGameResponse);
                    storeSession(joinGameResponse);
                }));
    }

    private void initStompListeners(JoinGameResponse joinResponse){
        stompManager.listenOnGame(joinResponse.getGameID());
        stompManager.listenPrivate();
    }

    private void wireUpLiveDataSources(JoinGameResponse joinGameResponse){
        Flowable<PlayersInLobbyDto> flowable = stompManager.filterByType(PlayersInLobbyDto.class);
        playerRepository.setCurrentPlayerID(joinGameResponse.getInGameID());
        playerRepository.setLiveData(flowable);
    }

    private void storeSession(JoinGameResponse joinGameResponse){
        tokenRepository.storeToken(joinGameResponse.getToken());
        tokenRepository.storeGameID(joinGameResponse.getGameID());
        tokenRepository.storeInGameID(joinGameResponse.getInGameID());
    }


}