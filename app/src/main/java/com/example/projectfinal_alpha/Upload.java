package com.example.projectfinal_alpha;

public class Upload {
    private String ImageUrl;

    public Upload(){

    }
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
