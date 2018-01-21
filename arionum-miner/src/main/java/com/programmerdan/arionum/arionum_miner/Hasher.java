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

import java.math.BigInteger;

/**
 * Abstraction layer to allow multiple miner definitions.
 * 
 * 
 * @author ProgrammerDan (Daniel Boston)
 *
 */
public abstract class Hasher implements Runnable{

	protected Miner parent;

	public abstract void run();

	protected boolean active;
	protected String id;
	protected long hashCount;
	
	// local copy of data, updated "off thread"
	protected BigInteger difficulty;
	protected String data;
	protected long limit;
	protected String publicKey;
	
	// local stats stores, retrieved "off thread"
	protected long bestDL;
	protected long shares;
	protected long finds;
	protected long argonTime;
	protected long nonArgonTime;
	protected long hashesRecent;
	
	public long getBestDL() {
		return bestDL;
	}
	
	public long getShares() {
		return shares;
	}
	
	public long getFinds() {
		return finds;
	}
	
	public long getArgonTime() {
		return argonTime;
	}
	
	public long getNonArgonTime() {
		return nonArgonTime;
	}
	
	public long getHashesRecent() {
		return hashesRecent;
	}
	
	public void clearTimers() {
		synchronized(this) {
			argonTime = 0l;
			nonArgonTime = 0l;
			hashesRecent = 0l;
		}
	}
	
	public void update(BigInteger difficulty, String data, long limit, String publicKey) {
		this.difficulty = difficulty;
		if (!data.equals(this.data)) {
			bestDL = Long.MAX_VALUE;
		}
		this.data = data;
		this.limit = limit;
		this.publicKey = publicKey;
	}

	public Hasher(Miner parent, String id) {
		super();
		this.parent = parent;
		this.id = id;
		this.active = false;
		this.hashCount = 0l;
	}

	public long getHashes() {
		return this.hashCount;
	}

	public boolean isActive() {
		return active;
	}

}