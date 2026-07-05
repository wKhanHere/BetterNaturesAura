package net.wkhan.naturesaura_plus.data.auragen;

import com.google.gson.annotations.SerializedName;

public class OreSpawnRule { //Me touch this later

    @SerializedName("dimension")
    private String dimensionId;

    @SerializedName("base_block")
    private String baseBlockId;

    @SerializedName("result_blocks")
    private String resultBlockId; //Make this an array or smth

    @SerializedName("aura_cost")
    private int auraAmount;

}
