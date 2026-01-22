package Reseau.back.Counters;

import java.math.BigInteger;

public interface AfficheBestAmis {
    Long getAmiId();

    String getNationalite();
    String getEtablissement();
    BigInteger getNb_jaime_sur_mes_publications();
}
