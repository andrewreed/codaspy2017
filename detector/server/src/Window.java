public class Window {

  private Movie parentMovie;
	private short startIndex;

	// Constructor
	public Window(Movie parentMovie, short startIndex) {
		this.parentMovie = parentMovie;
		this.startIndex = startIndex;
	}

	public String getTitle() {
		return parentMovie.getTitle();
	}

	public short getBitrate() {
		return parentMovie.getBitrate();
	}

	public int[] getSegments() {
		return parentMovie.getSegments();
	}

	public int getStartIndex() {
		return (int)startIndex;
	}

	public float[] getKey() {
		int[] segments = parentMovie.getSegments();

		float[] key = new float[6];

		key[1] = 0.0f;
		key[2] = 0.0f;
		key[3] = 0.0f;
		key[4] = 0.0f;
		key[5] = 0.0f;

		for (int i = 0; i < 6; i++) {
			key[1] += segments[startIndex +  0 + i];
			key[2] += segments[startIndex +  6 + i];
			key[3] += segments[startIndex + 12 + i];
			key[4] += segments[startIndex + 18 + i];
			key[5] += segments[startIndex + 24 + i];
		}

		key[0] = key[1] + key[2] + key[3] + key[4] + key[5];

		key[1] = key[1] / key[0];
		key[2] = key[2] / key[0];
		key[3] = key[3] / key[0];
		key[4] = key[4] / key[0];
		key[5] = key[5] / key[0];

		return key;
	}

}
