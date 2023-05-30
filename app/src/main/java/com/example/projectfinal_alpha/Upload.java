package com.example.projectfinal_alpha;

/**
 * The Upload class represents an uploaded file and holds the URL of the uploaded image.
 */
public class Upload {
    private String ImageUrl;

    public Upload(){

    }

    /**
     * Constructor for the Upload class that sets the ImageUrl.
     *
     * @param imageUrl The URL of the uploaded image.
     */
    public Upload(String imageUrl){
        ImageUrl = imageUrl;
    }


    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }
}
