package com.group2.catan_android.data.live.game;


import com.group2.catan_android.gamelogic.enums.Hexagontype;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;


public class HexagonDto{
    private Hexagontype hexagontype;
    private ResourceDistribution resourceDistribution;
    private int value;
    private int id;

    private boolean hasRobber;

    public HexagonDto(Hexagontype hexagontype, ResourceDistribution resourceDistribution, int value, int id, boolean hasRobber) {
        this.hexagontype = hexagontype;
        this.resourceDistribution = resourceDistribution;
        this.value = value;
        this.id = id;
        this.hasRobber=hasRobber;
    }

    public Hexagontype getHexagontype() {
        return hexagontype;
    }

    public void setHexagontype(Hexagontype hexagontype) {
        this.hexagontype = hexagontype;
    }

    public boolean isHasRobber() {
        return hasRobber;
    }

    public void setHasRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }
    public HexagonDto() {
    }

    public Hexagontype getLocation() {
        return hexagontype;
    }

    public void setLocation(Hexagontype hexagontype) {
        this.hexagontype = hexagontype;
    }

    public ResourceDistribution getResourceDistribution() {
        return resourceDistribution;
    }

    public void setResourceDistribution(ResourceDistribution resourceDistribution) {
        this.resourceDistribution = resourceDistribution;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
