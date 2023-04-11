package com.cosimogiani.museum.transaction;

public interface TransactionManager {
	
	public <T> T doInTransaction(TransactionFunction<T> code);

}
