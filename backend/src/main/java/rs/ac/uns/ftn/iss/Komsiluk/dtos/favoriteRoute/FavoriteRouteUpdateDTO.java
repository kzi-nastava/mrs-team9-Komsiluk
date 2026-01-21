package rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FavoriteRouteUpdateDTO {
	
	@NotBlank
	@Size(min = 2, max = 500)
	private String title;
	 
	public FavoriteRouteUpdateDTO() {
		super();
	}
	 
	public String getTitle() {
		return title;
	}
	 
	public void setTitle(String title) {
		this.title = title;
	}
}
