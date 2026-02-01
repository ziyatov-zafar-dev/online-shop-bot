package uz.zafar.onlineshoptelegrambot.db.entity.ads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AdsResponse {
    private UUID id;
    private String title;
    private String description;
    private Boolean hasPhoto;
    private String photoUrl;
    private List<AdsButtonResponse>buttons;
    private LocalDateTime created;

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }



    public List<AdsButtonResponse> getButtons() {

        return buttons;
    }

    public void setButtons(List<AdsButtonResponse> buttons) {
        this.buttons = buttons;
    }

    public AdsResponse(UUID id, String title, String description, Boolean hasPhoto, String photoUrl, List<AdsButtonResponse> buttons, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.hasPhoto = hasPhoto;
        this.photoUrl = photoUrl;
        this.buttons = buttons;
        this.created = created;
    }
}
