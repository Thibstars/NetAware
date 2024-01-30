package com.github.thibstars.netaware.scanners;

import java.util.Collection;

/**
 * @author Thibault Helsmoortel
 */
public interface Scanner<I, O> {

    Collection<O> scan(I input);

}
