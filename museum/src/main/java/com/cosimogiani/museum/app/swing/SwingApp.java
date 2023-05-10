package com.cosimogiani.museum.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cosimogiani.museum.controller.Controller;
import com.cosimogiani.museum.service.ArtistService;
import com.cosimogiani.museum.service.ArtistServiceTransactional;
import com.cosimogiani.museum.service.WorkService;
import com.cosimogiani.museum.service.WorkServiceTransactional;
import com.cosimogiani.museum.transaction.TransactionManager;
import com.cosimogiani.museum.transaction.mongo.TransactionManagerMongo;
import com.cosimogiani.museum.view.swing.SwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class SwingApp implements Callable<Void> {
	
	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "museum";
	
	@Option(names = { "--collection-artists-name" }, description = "Collection artists name")
	private String collectionArtistsName = "artist";
	
	@Option(names = { "--collection-works-name" }, description = "Collection works name")
	private String collectionWorksName = "work";
	
	public static void main(String[] args) {
		new CommandLine(new SwingApp()).execute(args);
	}
	
	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				MongoClient client = new MongoClient(new ServerAddress(mongoHost, mongoPort));
				TransactionManager transactionManager = new TransactionManagerMongo(client, client.startSession(), databaseName, 
						collectionArtistsName, collectionWorksName);
				ArtistService artistService = new ArtistServiceTransactional(transactionManager);
				WorkService workService = new WorkServiceTransactional(transactionManager);
				SwingView view = new SwingView();
				Controller controller = new Controller(view, artistService, workService);
				view.setController(controller);
				view.setVisible(true);
				controller.viewInitialization();
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}

}
