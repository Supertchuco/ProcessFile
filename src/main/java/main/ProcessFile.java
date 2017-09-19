package main;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProcessFile implements CommandLineRunner {

	private static FileService fileService;

	@Override
	public void run(String... args) throws Exception {
		main(args);
	}

	/**
	 * to do - research why autowired didn't working with this class
	 */
	@PostConstruct
	public void init() {
		fileService = new FileService();
	}

	public static void main(String[] args) throws Exception {
		System.out.println("monitoring started");
		// The monitor will perform polling on the folder every 5 seconds
		final long pollingInterval = 5 * 1000;

		File folder = new File(FileService.input_path);

		if (!folder.exists()) {
			// Test to see if monitored folder exists
			throw new RuntimeException("Directory not found: " + FileService.input_path);
		}

		FileAlterationObserver observer = new FileAlterationObserver(folder);
		FileAlterationMonitor monitor = new FileAlterationMonitor(pollingInterval);
		FileAlterationListener listener = new FileAlterationListenerAdaptor() {
			// Is triggered when a file is created in the monitored folder
			@Override
			public void onFileCreate(File file) {

				// "file" is the reference to the newly created file
				try {
					System.out.println(file.isFile());
					System.out.println(file.canRead());

					System.out.println("File created: " + file.getCanonicalPath());

					if (org.apache.commons.lang.StringUtils.equals(
							FilenameUtils.getExtension(file.getName()).toUpperCase(), "DAT") && !file.isDirectory()
							&& !StringUtils.contains(FilenameUtils.getBaseName(file.getName()), "done")) {
						fileService.processFile(file);

					} else {
						System.out.println("File: " + file.getName() + "not allowed");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		};

		observer.addListener(listener);
		monitor.addObserver(observer);
		monitor.start();
	}

}
