package com.ctc.wstx.dtd;

import java.util.*;

/**
 * Model class that encapsulates set of sub-models, of which one (and only
 * one) needs to be matched.
 */
public class ChoiceModel
    extends ModelNode
{
    final ModelNode[] mSubModels;

    boolean mNullable = false;

    BitSet mFirstPos, mLastPos;

    /*
    ///////////////////////////////////////////////////
    // Life-cycle
    ///////////////////////////////////////////////////
     */

    protected ChoiceModel(ModelNode[] subModels)
    {
        super();
        mSubModels = subModels;
        boolean nullable = false;
        for (int i = 0, len = subModels.length; i < len; ++i) {
            if (subModels[i].isNullable()) {
                nullable = true;
                break;
            }
        }
        mNullable = nullable;
    }

    /*
    ///////////////////////////////////////////////////
    // Public API
    ///////////////////////////////////////////////////
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mSubModels.length; ++i) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(mSubModels[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Method that has to create a deep copy of the model, without
     * sharing any of existing Objects.
     */
    public ModelNode cloneModel()
    {
        int len = mSubModels.length;
        ModelNode[] newModels = new ModelNode[len];
        for (int i = 0; i < len; ++i) {
            newModels[i] = mSubModels[i].cloneModel();
        }
        return new ChoiceModel(newModels);
    }

    public boolean isNullable() {
        return mNullable;
    }

    public void indexTokens(List tokens)
    {
        // First, let's ask sub-models to calc their settings
        for (int i = 0, len = mSubModels.length; i < len; ++i) {
            mSubModels[i].indexTokens(tokens);
        }
    }

    public void addFirstPos(BitSet firstPos) {
        if (mFirstPos == null) {
            mFirstPos = new BitSet();
            for (int i = 0, len = mSubModels.length; i < len; ++i) {
                mSubModels[i].addFirstPos(mFirstPos);
            }
        }
        firstPos.or(mFirstPos);
    }

    public void addLastPos(BitSet lastPos) {
        if (mLastPos == null) {
            mLastPos = new BitSet();
            for (int i = 0, len = mSubModels.length; i < len; ++i) {
                mSubModels[i].addLastPos(mLastPos);
            }
        }
        lastPos.or(mLastPos);
    }

    public void calcFollowPos(BitSet[] followPosSets)
    {
        // need to let child models do their stuff:
        for (int i = 0, len = mSubModels.length; i < len; ++i) {
            mSubModels[i].calcFollowPos(followPosSets);
        }
    }
}
