package com.me.vietlott.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Func1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.me.vietlott.om.Agent;
import com.me.vietlott.om.TransactionRequest;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.GsonUtils;

/**
 * @author lamhm
 *
 */
public class CacheService {
	private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);
	private static final String PRE_ORDER_TICKET_KEY = "order_ticket_";
	private static CacheService instance;
	private Cluster cluster;
	private Bucket bucket;
	private Map<Integer, Agent> agentMap;


	public static synchronized CacheService getInstance() {
		if (instance == null) {
			instance = new CacheService();
		}

		return instance;
	}


	private CacheService() {
		try {
			LOG.info("---------------- Start CacheService -----------");
			CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().connectTimeout(Configs.couchbaseConnectionTimeout)
					.kvTimeout(Configs.couchbaseOpTimeout).build();

			cluster = CouchbaseCluster.create(env, Configs.couchbaseHosts);
			bucket = cluster.openBucket(Configs.couchbaseBucket, Configs.couchbasePass);
			agentMap = new HashMap<Integer, Agent>();
			if (bucket == null) {
				LOG.error("[ERROR] Cache service can't get bucket");
			}

			LOG.info("---------------- CacheService Started -----------");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}


	private String getOrderTicketKey(int userId) {
		return PRE_ORDER_TICKET_KEY + userId;
	}


	public void putAgent(int userId, Agent agent) {
		agentMap.put(userId, agent);
	}


	public Agent getAgent(int userId) {
		return agentMap.get(userId);
	}


	/**
	 * Kiểm tra user này có yêu cầu đặt vé không
	 * 
	 * @param userId định danh user đặt vé
	 * @return
	 */
	public boolean isExistTransactionRequest(int userId) {
		return bucket.exists(getOrderTicketKey(userId));
	}


	/**
	 * Lưu trạng thái user này đã đặt vé, và được xử lý bởi đại lý nào
	 * 
	 * @param userId định danh user đặt vé
	 * @param info thông tin yêu cầu giao dịch
	 */
	public void upsertTransactionRequest(int userId, TransactionRequest info) {
		upsert(getOrderTicketKey(userId), Configs.transRequestSecondsTTL, GsonUtils.toGsonString(info));
	}


	/**
	 * Xóa yêu cầu mua vé
	 * 
	 * @param userId
	 */
	public void deleteTransactionRequest(int userId) {
		delete(getOrderTicketKey(userId));
	}


	/**
	 * Lấy yêu cầu giao dịch của user
	 * 
	 * @param userId
	 * @return <code>null</code> không tồn tại giao dịch của user này
	 */
	public TransactionRequest getTransactionRequest(int userId) {
		String transData = get(getOrderTicketKey(userId));
		if (transData == null)
			return null;

		return GsonUtils.fromGsonString(transData, TransactionRequest.class);
	}


	public void upsert(String key, String jsonString) {
		upsert(key, 0, jsonString);
	}


	public void upsert(String key, int expireSecond, String jsonString) {
		bucket.upsert(RawJsonDocument.create(key, expireSecond, jsonString));
	}


	public String get(String key) {
		RawJsonDocument json = bucket.get(key, RawJsonDocument.class);
		if (json != null) {
			return json.content();
		}

		return null;
	}


	public void delete(String key) {
		bucket.remove(key);
	}


	public List<RawJsonDocument> getBulk(final Collection<String> keys) {
		return Observable.from(keys).flatMap(new Func1<String, Observable<RawJsonDocument>>() {
			@Override
			public Observable<RawJsonDocument> call(String id) {
				return bucket.async().get(id, RawJsonDocument.class);
			}
		}).toList().toBlocking().single();
	}


	public void shutdown() {
		LOG.info("Destroy extension - Shutdown Couchbase");
		if (cluster != null) {
			bucket.close();
			cluster.disconnect();
		}
	}
}
