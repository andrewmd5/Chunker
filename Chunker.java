import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Chunker {

	public static void chunkSong() {

		final File file = new File("times.txt");
		final String fileName = "largeSong.mp3";
		try {
			final BufferedReader bufferReader = new BufferedReader(
					new FileReader(file));
			String line = null;
			final ArrayList<String> songs = new ArrayList<String>();
			while ((line = bufferReader.readLine()) != null) {
				songs.add(line);
			}

			for (int i = 0; i < songs.size(); i++) {
				final String[] data = songs.get(i).split("\\|");
				final String trackNumber = data[0];
				final String time = data[1];
				final String title = trackNumber + "-" + data[2];

				final int nextSong = i + 1;
				if (nextSong < songs.size()) {
					final String nextTime = songs.get(nextSong).split("\\|")[1]; //This gets the starting time of the next song in the main MP3
																			
					final SimpleDateFormat sdf = new SimpleDateFormat(
							"HH:mm:ss");
					final Date currentSongTime = sdf.parse(time);
					final Date nextSongTime = sdf.parse(nextTime);

					final long duration = nextSongTime.getTime()
							- currentSongTime.getTime(); // calculate how long  the current song  is

					final long diffInSeconds = TimeUnit.MILLISECONDS
							.toSeconds(duration); // ffmpeg uses the total
													// seconds you want to pass
													// before cutting the file

					String command = "ffmpeg -i %s -ss %s -t %s -acodec copy %s.mp3";

					command = String.format(command, fileName, time,
							diffInSeconds, "\"" + title + "\""); // generate
																	// command

					final Runtime rt = Runtime.getRuntime();
					final Process proc = rt.exec(command);
					proc.destroy();
				}
			}
			bufferReader.close();
		} catch (final IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		chunkSong();
	}
}
