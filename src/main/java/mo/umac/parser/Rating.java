package mo.umac.parser;

public class Rating {
	/**
	 * Average rating may be NaN, cannot be converted to Integer directly
	 */
	private String AverageRating;
	private int TotalRatings;
	private int TotalReviews;
	private String LastReviewDate;
	private String LastReviewIntro;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Rating [AverageRating=" + AverageRating + ", TotalRatings="
				+ TotalRatings + ", TotalReviews=" + TotalReviews
				+ ", LastReviewDate=" + LastReviewDate + ", LastReviewIntro="
				+ LastReviewIntro + "]");
		return sb.toString();
	}

	public String getAverageRating() {
		return AverageRating;
	}

	public void setAverageRating(String averageRating) {
		AverageRating = averageRating;
	}

	public int getTotalRatings() {
		return TotalRatings;
	}

	public void setTotalRatings(int totalRatings) {
		TotalRatings = totalRatings;
	}

	public int getTotalReviews() {
		return TotalReviews;
	}

	public void setTotalReviews(int totalReviews) {
		TotalReviews = totalReviews;
	}

	public String getLastReviewDate() {
		return LastReviewDate;
	}

	public void setLastReviewDate(String lastReviewDate) {
		LastReviewDate = lastReviewDate;
	}

	public String getLastReviewIntro() {
		return LastReviewIntro;
	}

	public void setLastReviewIntro(String lastReviewIntro) {
		LastReviewIntro = lastReviewIntro;
	}

}
