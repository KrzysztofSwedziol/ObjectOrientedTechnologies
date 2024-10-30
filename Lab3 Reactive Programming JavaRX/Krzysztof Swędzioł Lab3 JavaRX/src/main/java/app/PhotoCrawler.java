package app;

import io.reactivex.rxjava3.core.Observable;
import model.Photo;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            Observable<Photo> downloadedExamples = photoDownloader.getPhotoExamples();

            downloadedExamples
                    .doOnNext(photo -> photoSerializer.savePhoto(photo))
                    .subscribe(
                            photo -> {},
                            e -> System.out.println("there was an error: " + e)
                    );
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) throws IOException {
        try {
            Observable<Photo> source = photoDownloader.searchForPhotos(query);
            source.subscribe(
                    photo -> System.out.println(photo.toString()),
                    e -> System.out.println("there was an error: " + e),
                    () -> System.out.println("Stream Completed"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadPhotosForMultipleQueries(List<String> queries) {
        try {
            Observable<Photo> source = photoDownloader.searchForPhotos(queries);
            source.subscribe(
                    photo -> System.out.println(photo.toString()),
                    e -> System.out.println("there was an error: " + e),
                    () -> System.out.println("Stream Completed")
            );
            Thread.sleep(100_000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
