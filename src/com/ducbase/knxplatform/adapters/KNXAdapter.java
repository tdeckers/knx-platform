package com.ducbase.knxplatform.adapters;

import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * 
 * TODO:
 * <li>This class should probably by a subclass of Adapter (to create)</li>
 * 
 * @author tom@ducbase.com
 *
 */
public class KNXAdapter {

	static Logger logger = Logger.getLogger(KNXAdapter.class.getName());
	
	static final String CACHE_NAME = "knx-cache";
	
	/**
	 * a KNXAdapter maintains a cache of group address states.  Mainly for those group addresses used for Device state's.
	 */
	private Cache cache;
	
	/**
	 * Create a KNXAdapter
	 * 
	 * TODO: make singleton
	 */
	public KNXAdapter() {
		logger.fine("Creating cache");
		CacheManager cacheMgr = CacheManager.create();
		cacheMgr.addCache(CACHE_NAME);
		cache = cacheMgr.getCache(CACHE_NAME);		
	}
	
	/**
	 * Start the adapter operation
	 */
	public void start() {
		// connect to KNX, monitor connection
		// start cache
		// continuously update cache
		
	}

}
