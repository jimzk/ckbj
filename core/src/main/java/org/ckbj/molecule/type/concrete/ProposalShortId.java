package org.ckbj.molecule.type.concrete;

import org.ckbj.molecule.type.base.Array;
import org.ckbj.molecule.type.base.MoleculeException;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ProposalShortId extends Array {
    public static Class ITEM_TYPE = byte.class;

    public static int ITEM_SIZE = 1;

    public static int ITEM_COUNT = 10;

    public static int SIZE = ITEM_SIZE * ITEM_COUNT;

    private byte[] items;

    private ProposalShortId() {
    }

    @Nonnull
    public byte get(int i) {
        return items[i];
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    @Override
    public int getItemSize() {
        return ITEM_SIZE;
    }

    @Override
    public Class getItemType() {
        return ITEM_TYPE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull byte[] buf) {
        return new Builder(buf);
    }

    public static final class Builder {
        private byte[] items;

        private Builder() {
            items = new byte[ITEM_COUNT];
        }

        private Builder(@Nonnull byte[] buf) {
            Objects.requireNonNull(buf);
            if (buf.length != SIZE) {
                throw new MoleculeException(SIZE, buf.length, ProposalShortId.class);
            }
            items = new byte[ITEM_COUNT];
            items = buf;
        }

        public Builder set(int i, @Nonnull byte item) {
            Objects.requireNonNull(item);
            items[i] = item;
            return this;
        }

        public ProposalShortId build() {
            ProposalShortId a = new ProposalShortId();
            a.buf = items;
            a.items = items;
            return a;
        }
    }
}