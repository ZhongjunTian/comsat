/*
 * COMSAT
 * Copyright (C) 2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
package co.paralleluniverse.fibers.dropwizard;

import co.paralleluniverse.fibers.jdbc.FiberDataSource;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.dropwizard.db.ManagedDataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FiberManagedDataSource extends FiberDataSource implements ManagedDataSource {
    private final ManagedDataSource myds;

    public static ManagedDataSource wrap(ManagedDataSource ds, int numThreads) {
        return wrap(ds, Executors.newFixedThreadPool(numThreads, new ThreadFactoryBuilder().setNameFormat("jdbc-worker-%d").setDaemon(true).build()));
    }

    public static ManagedDataSource wrap(ManagedDataSource ds, ExecutorService executor) {
        return new FiberManagedDataSource(ds, MoreExecutors.listeningDecorator(executor));
    }

    protected FiberManagedDataSource(ManagedDataSource ds, ListeningExecutorService exec) {
        super(ds, exec);
        this.myds = ds;
    }

    @Override
    public void start() throws Exception {
        myds.start();
    }

    @Override
    public void stop() throws Exception {
        myds.stop();
    }

}
