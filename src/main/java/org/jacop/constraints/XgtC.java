/**
 * XgtC.java
 * This file is part of JaCoP.
 * <p>
 * JaCoP is a Java Constraint Programming solver.
 * <p>
 * Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * Notwithstanding any other provision of this License, the copyright
 * owners of this work supplement the terms of this License with terms
 * prohibiting misrepresentation of the origin of this work and requiring
 * that modified versions of this work be marked in reasonable ways as
 * different from the original version. This supplement of the license
 * terms is in accordance with Section 7 of GNU Affero General Public
 * License version 3.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jacop.constraints;

import java.util.concurrent.atomic.AtomicInteger;

import org.jacop.core.Domain;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.core.Var;

/**
 * Constraint X {@literal >} C
 *
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 4.4
 */

public class XgtC extends PrimitiveConstraint {

    static AtomicInteger idNumber = new AtomicInteger(0);

    /**
     * It specifies variable x which must be greater than a given constraint.
     */
    public IntVar x;

    /**
     * It specifies a constant C from which a given variable must be greater.
     */
    public int c;

    /**
     * It specifies the arguments required to be saved by an XML format as well as
     * the constructor being called to recreate an object from an XML format.
     */
    public static String[] xmlAttributes = {"x", "c"};

    /**
     * It constructs constraint X {@literal >} C.
     * @param x variable x.
     * @param c constant c.
     */
    public XgtC(IntVar x, int c) {

        assert (x != null) : "Variable x is null";

        numberId = idNumber.incrementAndGet();
        numberArgs = 1;

        this.x = x;
        this.c = c;

        setScope(x);

    }

    @Override public void consistency(Store store) {
        x.domain.inMin(store.level, x, c + 1);
    }

    @Override public int getNestedPruningEvent(Var var, boolean mode) {

        // If consistency function mode
        if (mode) {
            if (consistencyPruningEvents != null) {
                Integer possibleEvent = consistencyPruningEvents.get(var);
                if (possibleEvent != null)
                    return possibleEvent;
            }
            return IntDomain.BOUND;
        }
        // If notConsistency function mode
        else {
            if (notConsistencyPruningEvents != null) {
                Integer possibleEvent = notConsistencyPruningEvents.get(var);
                if (possibleEvent != null)
                    return possibleEvent;
            }
            return IntDomain.BOUND;
        }
    }

    @Override public int getConsistencyPruningEvent(Var var) {

        // If consistency function mode
        if (consistencyPruningEvents != null) {
            Integer possibleEvent = consistencyPruningEvents.get(var);
            if (possibleEvent != null)
                return possibleEvent;
        }
        return Domain.NONE;
    }

    @Override public int getNotConsistencyPruningEvent(Var var) {

        // If notConsistency function mode
        if (notConsistencyPruningEvents != null) {
            Integer possibleEvent = notConsistencyPruningEvents.get(var);
            if (possibleEvent != null)
                return possibleEvent;
        }
        return Domain.NONE;
    }

    @Override public void impose(Store store) {
        x.putModelConstraint(this, getConsistencyPruningEvent(x));
        store.addChanged(this);
        store.countConstraint();
    }

    @Override public void notConsistency(Store store) {
        x.domain.inMax(store.level, x, c);
    }

    @Override public boolean notSatisfied() {
        return x.max() <= c;
    }

    @Override public void removeConstraint() {
        x.removeConstraint(this);
    }

    @Override public boolean satisfied() {
        return x.min() > c;
    }

    @Override public String toString() {
        return id() + " : XgtC(" + x + ", " + c + " )";
    }

    @Override public void increaseWeight() {
        if (increaseWeight) {
            x.weight++;
        }
    }

}
