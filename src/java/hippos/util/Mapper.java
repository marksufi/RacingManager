package hippos.util;

import hippos.RaceProgramHorse;
import hippos.exception.KeyNotFoundException;
import hippos.lang.stats.SubForm;
import hippos.math.Value;
import hippos.math.regression.HipposUpdatingRegression;

import java.math.BigDecimal;
import java.util.*;

/**
 * Luokka joka tallentaa alkiokarttaan annetun arvon. Talletettavan
 * arvon luokka määritetään luokan alustuksessa
 *
 * Mapper <String> Stringmapper = new Mapper();
 * Mapper <BigDecimal> Bigdecimalmapper = new Mapper();
 * Mapper <Object> whatevermapper = new Mapper();
 *
 * Esim. alkiolista on [1], jonne ei ole tallennttu mitään mutta jota haetaan, palauttaa null
 * mapper.get([1]) palauttaa null
 *
 * jos alkiolista löytyy puusta, palauttaa tallennetun arvon
 * mapper.add([1], 1.0)
 * mapper.get([1]) palauttaa 1.00
 *
 * mapper.add([1, 2], 2.0)
 * mapper.get([1, 2]) palauttaa 2.00
 *
 * Mapper <String> esimerkki = new Mapper();
 * List avainlista = Arrays.asList("eka", "toka", "kolmas");
 * esimerkki.add(avainlista, "kolme avainta");
 * String tallennettuarvo = esimerkki.get(avainlista);
 * System.out.println(avainlista + " => " + tallennettuarvo);
 *
 * tulostaa [eka, toka, kolmas] => kolme avainta
 *
 * esimerkki.add(avainlista, null);
 * esimerkki.get(avainlista) palauttaa null;
 *
 * @param <T>
 *     Tallennettavan tiedon luokka
 */

public class Mapper<T> {
    private MapperCell <T> initMap;
    private Object iKey;

    public <T> Mapper() {
        initMap  = new MapperCell<>();
    }

    /**
     * Hakee avainlistalla tallennettua arvoa
     *
     * @param keys  Lista avaimista josta tallennettua arvoa haetaan
     * @return      Tannettu arvo, jos löytyy, muuten null
     */
    public T get(List keys) {
        return get(initMap, keys.iterator(), null);
    }

    public T get(Object key)  {
        return get(initMap, Collections.singletonList(key).iterator(), null);
    }

    /**
     * Hakee avainlistalla tallennettua arvoa
     *
     * @param keys  Lista avaimista josta tallennettua arvoa haetaan
     * @param newT  Uusi tallennettava tieto, jos sitä ei löydy ennestään
     *
     * @return  Tannettu arvo, jos löytyy, muuten uusi newT tietue
     */
    public T getOrCreate(List keys, T newT) {

        return get(initMap, keys.iterator(), newT);
    }

    public T getOrCreate(Object key, T newT) {

        return get(initMap, Collections.singletonList(key).iterator(), newT);
    }

    private T get(MapperCell map, Iterator keyItr, T newT) {
        try {
            if(keyItr.hasNext()) {
                iKey = keyItr.next();
            } else {
                return (T) map.getValue(iKey, newT);
            }

            return get((MapperCell)map.next(iKey), keyItr, newT);
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lisää hakutaulukkoon avainlistalla määritetyyn paikkaan parametrina annetun arvon
     *
     * @param keys  Lista avaimista jonne tieto talletetaan
     * @param value Tallennettava tieto
     */
    public void put(List keys, T value) {
        add(initMap, keys.iterator(), value);
    }

    public void put(Object key, T value) {
        add(initMap, Collections.singletonList(key).iterator(), value);
    }

    private void add(MapperCell keyMap, Iterator keyItr, T value) {
        if(keyItr.hasNext()) {
            iKey = keyItr.next();
        } else  {
            keyMap.put(iKey, value);
            return;
        }
        add((MapperCell)keyMap.next(iKey), keyItr, value);
    }

    public Collection<T> getValues() {
        return initMap.valuemap.values();
    }

    public Collection getKeys() {
        return initMap.cellMap.keySet();
    }

    private class MapperCell<T> {
        TreeMap <Object, MapperCell> cellMap = new TreeMap<>();
        TreeMap <Object, T> valuemap = new TreeMap<>();

        public T getValue(Object iKey, T newT) {
            if(iKey == null)
                return newT;

            if(newT != null && !valuemap.containsKey(iKey))
                valuemap.put(iKey, newT);

            return valuemap.get(iKey);
        }

        public MapperCell next(Object iKey) {
            if(!cellMap.containsKey(iKey))
                cellMap.put(iKey, new MapperCell());
            return cellMap.get(iKey);
        }

        public void put(Object iKey, T value) {
            try {
                valuemap.put(iKey, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public T get(Object key)  {
            try {
                return valuemap.get(key);
            } catch (NullPointerException e) {
                // Avainta ei löyvy
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String toString() {
            return valuemap.values().toString();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(Object key : initMap.cellMap.keySet()) {
            sb.append(key.toString()  + ":" + initMap.cellMap.get(key) + "\n");
        }

        return sb.toString();
    }

    public static void main(String args []) {
        List keyList = new ArrayList();

        Mapper <Value> mapper = new Mapper();

        // tyhjä
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));

        //
        //mapper.put(BigDecimal.ZERO, new Value(0));
        //System.out.println("Mapper.main " + BigDecimal.ZERO + " => " + mapper.get(BigDecimal.ZERO));
        //System.out.println("Mapper.main " + BigDecimal.ZERO + " => " + mapper.getOrCreate(BigDecimal.ZERO, new Value(100)));

        //keyList.add("eka");
        keyList.add(new BigDecimal(1));
        mapper.put(keyList, new Value(1.0));
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));

        //System.out.println("Mapper.main " + BigDecimal.valueOf(1) + " => " + mapper.get(BigDecimal.valueOf(1)));
        //System.out.println("Mapper.main " + BigDecimal.valueOf(1) + " => " + mapper.getOrCreate(BigDecimal.valueOf(1), new Value(101)));
        //System.out.println("Mapper.main " + BigDecimal.valueOf(2) + " => " + mapper.getOrCreate(BigDecimal.valueOf(2), new Value(102)));

        keyList.add("toka");
        mapper.put(keyList, new Value(2.0));
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));

        keyList.add(new BigDecimal(3));
        mapper.put(keyList, new Value(3.0));
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));

        mapper.get(keyList).add(4);
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));

        keyList.add("neljäs");
        mapper.getOrCreate(keyList, new Value()).add(4);
        System.out.println("Mapper.main " + keyList + " => " + mapper.get(keyList));


    }

}
