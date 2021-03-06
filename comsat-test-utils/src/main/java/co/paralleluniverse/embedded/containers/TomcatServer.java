/*
 * COMSAT
 * Copyright (C) 2014-2016, Parallel Universe Software Co. All rights reserved.
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
package co.paralleluniverse.embedded.containers;

import java.io.File;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;

public final class TomcatServer extends AbstractEmbeddedServer {
    private static final String defaultResDir = System.getProperty(TomcatServer.class.getName() + ".defaultResDir", "./build");

    protected final Tomcat tomcat;
    protected final Context context;

    public TomcatServer() {
        this(defaultResDir);
    }

    public TomcatServer(String resDir) {
        tomcat = new Tomcat();
        context = tomcat.addContext("/", new File(resDir).getAbsolutePath());
    }

    @Override
    public final ServletDesc addServlet(String name, Class<? extends Servlet> servletClass, String mapping) {
        final Wrapper w = Tomcat.addServlet(context, name, servletClass.getName());
        w.addMapping(mapping);
        return new TomcatServletDesc(w);
    }

    @Override
    public final void start() throws Exception {
        tomcat.setPort(port);

        tomcat.getConnector().setAttribute("maxThreads", nThreads);
        tomcat.getConnector().setAttribute("acceptCount", maxConn);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public final void run() {
                try {
                    tomcat.stop();
                } catch (LifecycleException ignored) {}
            }
        });

        tomcat.start();

        new Thread() {
            @Override
            public final void run() {
                tomcat.getServer().await();
            }
        }.start();
    }

    @Override
    public final void stop() throws Exception {
        tomcat.stop();
        tomcat.getConnector().destroy();
        tomcat.destroy();
    }

    @Override
    public final void enableWebsockets() throws Exception {
        context.addServletContainerInitializer(new WsSci(), null);
    }

    @Override
    public final void addServletContextListener(Class<? extends ServletContextListener> scl) {
        StandardContext tomcatCtx = (StandardContext) this.context;
        tomcatCtx.addApplicationListener(scl.getName());
    }

    @Override
    public final void setResourceBase(String resourceBaseUrl) {
        // TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static final class TomcatServletDesc implements ServletDesc {
        private final Wrapper impl;

        public TomcatServletDesc(Wrapper w) {
            this.impl = w;
        }

        @Override
        public final ServletDesc setInitParameter(String name, String value) {
            impl.addInitParameter(name, value);
            return this;
        }

        @Override
        public final ServletDesc setLoadOnStartup(int load) {
            impl.setLoadOnStartup(load);
            return this;
        }
    }
}
