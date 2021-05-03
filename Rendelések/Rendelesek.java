package com.zsolti.rendelesek;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static Rendeles r;
    static RendelesTermek rt;
    static List<Rendeles> rendelesek = new ArrayList<>();
    static List<RendelesTermek> termekrendelesek = new ArrayList<>();
    static List<Termek> termekek = new ArrayList<>();

    static List<String> sorok = new ArrayList<>();

    public static void main(String[] args) {

        try {
            BufferedReader br = new BufferedReader(new FileReader("raktar.csv"));
            String sor;

            while ((sor = br.readLine()) != null) {
                String[] szelet = sor.split(";");
                Termek t = new Termek();
                t.setCikkszam(szelet[0]);
                t.setNeve(szelet[1]);
                t.setAr(Integer.parseInt(szelet[2]));
                t.setKeszleten(Integer.parseInt(szelet[3]));
                termekek.add(t);
            }
            br = new BufferedReader(new FileReader("rendeles.csv"));

            while ((sor = br.readLine()) != null) {
                sorok.add(sor);
            }

            br.close();

            for (int i = 0; i < sorok.size(); i++) {

                if (sorok.get(i).charAt(0) == 'M') {
                    String[] szelet = sorok.get(i).split(";");
                    r = new Rendeles();
                    termekrendelesek = new ArrayList<>();
                    r.setDatum(szelet[1]);
                    r.setRendelesszama(Integer.parseInt(szelet[2]));
                    r.setEmail(szelet[3]);
                    rendelesek.add(r);
                } else {
                    String[] szelet = sorok.get(i).split(";");
                    rt = new RendelesTermek();
                    rt.setRendelesszama(Integer.parseInt(szelet[1]));
                    rt.setTermekcikk(szelet[2]);
                    rt.setMennyiseg(Integer.parseInt(szelet[3]));
                    termekrendelesek.add(rt);
                    r.setTermek(termekrendelesek);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Hiba a fájl beolvasás során!");
        }
        //beolvasás fasza
        for (Rendeles rendeles : rendelesek) {
            for (int i = 0; i < rendeles.getTermek().size(); i++) {
                for (Termek termek : termekek) {
                    if (termek.getCikkszam().equals(rendeles.getTermek().get(i).getTermekcikk())) {
                        if (termek.getKeszleten() > rendeles.getTermek().get(i).getMennyiseg()) {
                            rendeles.setKiadhato(true);
                            int ossz = rendeles.getOsszes();
                            ossz += rendeles.getTermek().get(i).getMennyiseg() * termek.getAr();
                            rendeles.setOsszes(ossz);
                            int keszlet = termek.getKeszleten() - rendeles.getTermek().get(i).getMennyiseg();
                            termek.setKeszleten(keszlet);
                        } else {
                            rendeles.setKiadhato(false);
                            int kell = termek.getKell();
                            kell += rendeles.getTermek().get(i).getMennyiseg();
                            termek.setKell(kell);
                        }
                    }
                }
            }
        }
        //levelek
        try {
            FileWriter fw = new FileWriter("levelek.csv");

            for (Rendeles rendeles : rendelesek) {
                if (rendeles.isKiadhato()) {
                    fw.write(rendeles.getEmail() + ";A rendelését két napon belül szállítjuk. A rendelés értéke: " + rendeles.getOsszes() + " Ft\n");
                } else {
                    fw.write(rendeles.getEmail() + ";A rendelése függő állapotba került. Hamarosan értesítjük a szállítás időpontjáról.\n");
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
        }

        //beszerzes
        try {
            FileWriter fw = new FileWriter("beszerzes.csv");

            for (Termek termek : termekek) {
                if (termek.getKell() != 0) {
                    fw.write(termek.getCikkszam() + ";" + termek.getKell() + "\n");
                }
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
        }
    }
}

class Rendeles {

    private String datum;
    private String email;
    private int rendelesszama;
    private List<RendelesTermek> termek = new ArrayList<>();
    private int osszes = 0;
    private boolean kiadhato;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRendelesszama() {
        return rendelesszama;
    }

    public void setRendelesszama(int rendelesszama) {
        this.rendelesszama = rendelesszama;
    }

    public List<RendelesTermek> getTermek() {
        return termek;
    }

    public void setTermek(List<RendelesTermek> termek) {
        this.termek = termek;
    }

    public int getOsszes() {
        return osszes;
    }

    public void setOsszes(int osszes) {
        this.osszes = osszes;
    }

    public boolean isKiadhato() {
        return kiadhato;
    }

    public void setKiadhato(boolean kiadhato) {
        this.kiadhato = kiadhato;
    }
}

class RendelesTermek {

    private int rendelesszama;
    private String termekcikk;
    private int mennyiseg;

    public int getRendelesszama() {
        return rendelesszama;
    }

    public void setRendelesszama(int rendelesszama) {
        this.rendelesszama = rendelesszama;
    }

    public String getTermekcikk() {
        return termekcikk;
    }

    public void setTermekcikk(String termekcikk) {
        this.termekcikk = termekcikk;
    }

    public int getMennyiseg() {
        return mennyiseg;
    }

    public void setMennyiseg(int mennyiseg) {
        this.mennyiseg = mennyiseg;
    }
}

class Termek {

    private String cikkszam;
    private String neve;
    private int ar;
    private int keszleten;
    private int kell = 0;

    public String getCikkszam() {
        return cikkszam;
    }

    public void setCikkszam(String cikkszam) {
        this.cikkszam = cikkszam;
    }

    public String getNeve() {
        return neve;
    }

    public void setNeve(String neve) {
        this.neve = neve;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public int getKeszleten() {
        return keszleten;
    }

    public void setKeszleten(int keszleten) {
        this.keszleten = keszleten;
    }

    public int getKell() {
        return kell;
    }

    public void setKell(int kell) {
        this.kell = kell;
    }
}
