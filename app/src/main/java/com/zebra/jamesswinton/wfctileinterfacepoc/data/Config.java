
package com.zebra.jamesswinton.wfctileinterfacepoc.data;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("api_password")
    @Expose
    private String apiPassword;
    @SerializedName("iwg_url")
    @Expose
    private String iwgUrl;
    @SerializedName("columns")
    @Expose
    private int columns;
    @SerializedName("tiles")
    @Expose
    private List<Tile> tiles = new ArrayList<Tile>();

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getIwgUrl() {
        return iwgUrl;
    }

    public void setIwgUrl(String iwgUrl) {
        this.iwgUrl = iwgUrl;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public static class Tile {

        @SerializedName("ui_config")
        @Expose
        private UiConfig uiConfig;
        @SerializedName("iwg_config")
        @Expose
        private IwgConfig iwgConfig;

        public UiConfig getUiConfig() {
            return uiConfig;
        }

        public void setUiConfig(UiConfig uiConfig) {
            this.uiConfig = uiConfig;
        }

        public IwgConfig getIwgConfig() {
            return iwgConfig;
        }

        public void setIwgConfig(IwgConfig iwgConfig) {
            this.iwgConfig = iwgConfig;
        }

        public static class IwgConfig {

            @SerializedName("eid")
            @Expose
            private int[] eid;
            @SerializedName("message")
            @Expose
            private String message;
            @SerializedName("attachment_type")
            @Expose
            private String attachmentType;
            @SerializedName("file_path")
            @Expose
            private String filePath;

            public int[] getEid() {
                return eid;
            }

            public void setEid(int[] eid) {
                this.eid = eid;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getAttachmentType() {
                return attachmentType;
            }

            public void setAttachmentType(String attachmentType) {
                this.attachmentType = attachmentType;
            }

            public String getFilePath() {
                return filePath;
            }

            public void setFilePath(String filePath) {
                this.filePath = filePath;
            }
        }

        public static class UiConfig {

            @SerializedName("text")
            @Expose
            private String text;
            @SerializedName("text_colour")
            @Expose
            private String textColour;
            @SerializedName("text_size")
            @Expose
            private int textSize;
            @SerializedName("background_colour")
            @Expose
            private String backgroundColour;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getTextColour() {
                return textColour;
            }

            public void setTextColour(String textColour) {
                this.textColour = textColour;
            }

            public int getTextSize() {
                return textSize;
            }

            public void setTextSize(int textSize) {
                this.textSize = textSize;
            }

            public String getBackgroundColour() {
                return backgroundColour;
            }

            public void setBackgroundColour(String backgroundColour) {
                this.backgroundColour = backgroundColour;
            }
        }

    }

}
