package com.rustamnavoyan.theguardiannewsfeed.models.data;

public class Result {
    private String id;
    private String type;
    private String sectionId;
    private String sectionName;
    private String webPublicationDate;
    private String webTitle;
    private String webUrl;
    private String apiUrl;
    private Boolean isHosted;
    private Boolean pillarId;
    private String pillarName;
    private Fields fields;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public Boolean getHosted() {
        return isHosted;
    }

    public Boolean getPillarId() {
        return pillarId;
    }

    public String getPillarName() {
        return pillarName;
    }

    public Fields getFields() {
        return fields;
    }
}
