// 
// Decompiled by Procyon v0.5.36
// 

package rs.etf.sab.tests;

public class Pair<K, V>
{
    private final K element0;
    private final V element1;
    
    public static <K, V> Pair<K, V> createPair(final K element0, final V element1) {
        return new Pair<K, V>(element0, element1);
    }
    
    public Pair(final K element0, final V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }
    
    public K getKey() {
        return this.element0;
    }
    
    public V getValue() {
        return this.element1;
    }
}
