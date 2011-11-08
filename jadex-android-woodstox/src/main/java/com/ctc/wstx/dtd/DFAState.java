/* Woodstox XML processor
 *
 * Copyright (c) 2004 Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ctc.wstx.dtd;

import java.util.*;

import com.ctc.wstx.util.PrefixedName;

/**
 * Class that represents a state in DFA used for validating complex
 * DTD content models.
 */
public final class DFAState
{
    final int mIndex;
    final boolean mAccepting;

    BitSet mTokenSet;

    HashMap mNext = new HashMap();

    /*
    ///////////////////////////////////////////////
    // Life-cycle:
    ///////////////////////////////////////////////
     */

    public DFAState(int index, BitSet tokenSet)
    {
        mIndex = index;
        // If we have a transition to state 0, it is an accepting state...
        mAccepting = tokenSet.get(0);
        mTokenSet = tokenSet;
    }

    public static DFAState constructDFA(ContentSpec rootSpec)
    {
        // Let's first create the real model tree:
        ModelNode modelRoot = rootSpec.rewrite();

        /* Then we need to add the dummy end token, and concat node
         * to contain it:
         */
        TokenModel eofToken = TokenModel.getNullToken();
        ConcatModel dummyRoot = new ConcatModel(modelRoot, eofToken);

        /* then need to allocate index numbers for tokens 
         * (which will also calculate nullability)
         */
        ArrayList tokens = new ArrayList();
        tokens.add(eofToken); // has to be added first, explicitly
        dummyRoot.indexTokens(tokens);

        /* And then we can request calculation of follow pos; this will
         * also recursively calculate first/last pos as needed:
         */
        int flen = tokens.size();
        BitSet[] followPos = new BitSet[flen];
        PrefixedName[] tokenNames = new PrefixedName[flen];
        for (int i = 0; i < flen; ++i) {
            followPos[i] = new BitSet(flen);
            tokenNames[i] = ((TokenModel) tokens.get(i)).getName();
        }
        dummyRoot.calcFollowPos(followPos);

        /* And then we can calculate DFA stuff. First step is to get
         * firstpos set for the root node, for creating the first
         * state:
         */
        BitSet initial = new BitSet(flen);
        dummyRoot.addFirstPos(initial);
        DFAState firstState = new DFAState(0, initial);
        ArrayList stateList = new ArrayList();
        stateList.add(firstState);
        HashMap stateMap = new HashMap();
        stateMap.put(initial, firstState);

        int i = 0;
        while (i < stateList.size()) {
            DFAState curr = (DFAState) stateList.get(i++);
            curr.calcNext(tokenNames, followPos, stateList, stateMap);
        }

        // DEBUG:
        /*
        for (i = 0; i < stateList.size(); ++i) {
            //System.out.println(stateList.get(i));
        }
        */

        // And there we have it!
        return firstState;
    }    

    /*
    ///////////////////////////////////////////////
    // Public API, accessors:
    ///////////////////////////////////////////////
     */

    public boolean isAcceptingState() {
        return mAccepting;
    }

    public int getIndex() {
        return mIndex;
    }

    public DFAState findNext(PrefixedName elemName) {
        return (DFAState) mNext.get(elemName);
    }

    public TreeSet getNextNames() {
        // Let's order them alphabetically
        TreeSet names = new TreeSet();
        Iterator it = mNext.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            names.add(o);
        }
        return names;
    }

    public void calcNext(PrefixedName[] tokenNames, BitSet[] tokenFPs,
                         List stateList, Map stateMap)
    {
        /* Need to loop over all included tokens, and find groups
         * of said tokens
         */
        int first = -1;

        /* Need to clone; can not modify in place, since the BitSet
         * is also used as the key...
         */
        BitSet tokenSet = (BitSet) mTokenSet.clone();
        // No need to keep the reference to it, though:
        mTokenSet = null;

        while ((first = tokenSet.nextSetBit(first+1)) >= 0) {
            PrefixedName tokenName = tokenNames[first];

            /* Special case; the dummy end token has null as name;
             * we can skip that one:
             */
            if (tokenName == null) {
                continue;
            }

            BitSet nextGroup = (BitSet) tokenFPs[first].clone();
            int second = first;

            while ((second = tokenSet.nextSetBit(second+1)) > 0) {
                if (tokenNames[second] == tokenName) {
                    // Let's clear it, too, so we won't match it again:
                    tokenSet.clear(second);
                    nextGroup.or(tokenFPs[second]);
                }
            }

            // Ok; is it a new group?
            DFAState next = (DFAState) stateMap.get(nextGroup);
            if (next == null) { // yup!
                next = new DFAState(stateList.size(), nextGroup);
                stateList.add(next);
                stateMap.put(nextGroup, next);
            }
            mNext.put(tokenName, next);
        }
    }

    /*
    ///////////////////////////////////////////////
    // Other methods
    ///////////////////////////////////////////////
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("State #"+mIndex+":\n");
        sb.append("  Accepting: "+mAccepting);
        sb.append("\n  Next states:\n");
        Iterator it = mNext.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry en = (Map.Entry) it.next();
            sb.append(en.getKey());
            sb.append(" -> ");
            DFAState next = (DFAState) en.getValue();
            sb.append(next.getIndex());
            sb.append("\n");
        }
        return sb.toString();
    }
}
