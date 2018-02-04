/**
The MIT License (MIT)
Copyright (c) 2018 AroDev, adaptation portions (c) 2018 ProgrammerDan (Daniel Boston)

www.arionum.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of
the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
OR OTHER DEALINGS IN THE SOFTWARE.

 */
package com.programmerdan.arionum.arionum_miner;

import java.util.concurrent.ThreadFactory;

import net.openhft.affinity.AffinityLock;

public class AggressiveAffinityThreadFactory implements ThreadFactory {

    private final String name;
    private final boolean daemon;

    private int id = 1;

    public AggressiveAffinityThreadFactory(String name) {
        this(name, true);
    }

    public AggressiveAffinityThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }

    @Override
    public synchronized Thread newThread(final Runnable r) {
    	final int myid = id;
        String name2 = myid <= 1 ? name : (name + '-' + myid);
        id++;
        System.err.println("Creating a new thread: " + name2);
        Thread t = new Thread(new Runnable() {
			@Override
            public void run() {
                try {
                	if (myid < AffinityLock.cpuLayout().cpus()-1) {
	                	AffinityLock lock = AffinityLock.acquireLock(false); //myid);
	                	if (!lock.isAllocated()) {
	                		lock = AffinityLock.acquireLock();
	                	}
	                	if (!lock.isAllocated()) {
	                		lock = AffinityLock.acquireCore();
	                	}
	                	if (!lock.isAllocated()) {
	                		System.err.println("Thread " + name2 + " could not reserve a core, try with fewer hashers.");
	                	}
	                	
	                    r.run();
	                    
	                    lock.close();
                	} else {
                		System.err.println("Thread " + name2 + " tried to reserve a core out of range, try with fewer hashers.");
                		
                		r.run();
                	}
                }catch (Throwable e) {
                	System.err.println("Thread " + name2 + " died with error:");
                	e.printStackTrace();
				}
            }
        }, name2);
        t.setDaemon(daemon);
        return t;
}
	
}
