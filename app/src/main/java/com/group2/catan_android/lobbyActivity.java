package com.group2.catan_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.group2.catan_android.adapter.GameListAdapter;
import com.group2.catan_android.data.api.JoinGameRequest;
import com.group2.catan_android.data.model.AvailableGame;
import com.group2.catan_android.data.repository.lobby.LobbyRepository;
import com.group2.catan_android.data.service.GameController;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class lobbyActivity extends AppCompatActivity {
    private String selectedGameID = null;
    private String playerName = "";
    private EditText playerNameEditText;
    private GameListAdapter gameListAdapter;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobbyscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        compositeDisposable = new CompositeDisposable();

        playerNameEditText = findViewById(R.id.LobbyPlayerNameEditText);
        Button connectButton = findViewById(R.id.LobbyJoinButton);
        Button createButton = findViewById(R.id.LobbyCreateButton);
        ImageButton refreshButton = findViewById(R.id.LobbyRefreshButton);

        GameListAdapter.ItemClickListener listener = game -> {
            selectedGameID = game.getGameID().equals(selectedGameID) ? null : game.getGameID();
            updateButtonColors();
        };

        gameListAdapter = new GameListAdapter(listener);

        RecyclerView recyclerView = findViewById(R.id.LobbyGameRecyclerView);
        recyclerView.setAdapter(gameListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestActiveGames();
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                joinGame(false);
                Log.d("LobbyActivity", "Connect button pressed with playername: " + playerName + "and gameID: " + selectedGameID);
                requestActiveGames();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerName = playerNameEditText.getText().toString();
                joinGame(true);
                requestActiveGames();
                Log.d("LobbyActivity", "Create button pressed with playername: " + playerName);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestActiveGames();
            }
        });

    }

    private void requestActiveGames() {
        LobbyRepository repository = LobbyRepository.getInstance();
        selectedGameID = null;
        updateButtonColors();

        repository.getLobbies()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<AvailableGame>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onSuccess(List<AvailableGame> availableGames) {
                                gameListAdapter.setGames(availableGames);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(lobbyActivity.this, "Faild to fetch games", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void joinGame(boolean create) {
        GameController gc = GameController.getInstance();
        String playerName = playerNameEditText.getText().toString().trim();
        if (playerName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Player name is required", Toast.LENGTH_LONG).show();
            return;
        }
        JoinGameRequest request = new JoinGameRequest();
        request.setPlayerName(playerName);
        if (!create && selectedGameID != null) {
            request.setGameID(selectedGameID);
        }

        Completable completable = create ? gc.createGame(request) : gc.joinGame(request);

        completable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onComplete() {
                            Intent i = new Intent(getApplicationContext(), InLobby.class);
                            startActivity(i);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(lobbyActivity.this, "Failed to join game: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
    }

    private void updateButtonColors(){
        Button connectButton = findViewById(R.id.LobbyJoinButton);
        Button createButton = findViewById(R.id.LobbyCreateButton);

        if(selectedGameID != null){
            connectButton.setBackgroundColor(getResources().getColor(R.color.button_available));
            createButton.setBackgroundColor(getResources().getColor(R.color.button_not_available));
            createButton.setEnabled(false);
            connectButton.setEnabled(true);
        } else {
            connectButton.setBackgroundColor(getResources().getColor(R.color.button_not_available));
            createButton.setBackgroundColor(getResources().getColor(R.color.button_available));
            createButton.setEnabled(true);
            connectButton.setEnabled(false);
        }
    }

}