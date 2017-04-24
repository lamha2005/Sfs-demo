package com.me.vietlott.service;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lamhm
 *
 */
public class ExecutorManager {
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorManager.class);
	private static ExecutorManager _instance;


	public static ExecutorManager getInstance() {
		if (_instance == null) {
			_instance = new ExecutorManager();
		}
		return _instance;
	}

	private HashMap<String, ExecutorService> executors;
	private boolean isShutdown;

	private ExecutorService _excAsyncTasks;


	private ExecutorManager() {
		executors = new HashMap<String, ExecutorService>();
		isShutdown = false;

		// add size to config
	}


	public ExecutorService newThreadPool(int poolSize, String threadName) {
		ExecutorService executor = executors.get(threadName);
		if (executor == null) {
			// TODO put executor
		}
		return executor;
	}


	public void destroy() {
		LOG.info("Destroy extension - Shutdown all executors");

		isShutdown = true;
		for (ExecutorService executor : executors.values()) {
			executor.shutdownNow();
		}
		executors.clear();
		_excAsyncTasks.shutdownNow();
	}


	public boolean isShutdown() {
		return isShutdown;
	}


	public ExecutorService getPoolAsycTask() {
		return _excAsyncTasks;
	}

}
