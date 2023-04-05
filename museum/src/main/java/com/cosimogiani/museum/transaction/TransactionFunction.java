package com.cosimogiani.museum.transaction;

import java.util.function.BiFunction;

import com.cosimogiani.museum.repository.ArtistRepository;
import com.cosimogiani.museum.repository.WorkRepository;

public interface TransactionFunction<T> extends BiFunction<ArtistRepository, WorkRepository, T> {

}
