package net.crashcraft.crashclaim.data;

import net.crashcraft.crashclaim.CrashClaim;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

public class TimedHashSet<T> implements Iterable<T> {
    private final HashSet<TimedItem<T>> hashset = new HashSet<>();

    public TimedHashSet() {
        Bukkit.getScheduler().runTaskTimer(CrashClaim.getPlugin(), () ->
                hashset.removeIf(TimedItem::isExpired), 20, 20);
    }

    public void add(T item, long timeoutMillis) {
        hashset.add(new TimedItem<>(item, System.currentTimeMillis() + timeoutMillis));
    }

    public boolean isEmpty(){
        return hashset.isEmpty();
    }

    public boolean contains(T item) {
        return get(item).isPresent();
    }

    public Optional<TimedItem<T>> get(T item) {
        return hashset.stream().filter(tTimedItem -> tTimedItem.item == item).findFirst();
    }

    public HashSet<TimedItem<T>> get() {
        return hashset;
    }

    public static final class TimedItem<T> {
        private final T item;
        private final long expireTime;

        public TimedItem(T item, long expireTime) {
            this.item = item;
            this.expireTime = expireTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }

        public T item() {
            return item;
        }

        public long getLeftTime() {
            return expireTime - System.currentTimeMillis();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new TimedHashSetIterator<>(hashset.iterator());
    }

    private record TimedHashSetIterator<T>(Iterator<TimedItem<T>> iterator) implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        public T next() {
            return iterator.next().item();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
