package barqsoft.footballscores;

/**
 * Created by yehya khaled on 3/3/2015.
 */

import android.content.Context;

import java.text.Normalizer;

public class Utilies {

    /*******
     * Based on the API, representing leagues like this is BAD!!
     * You would need to update the ints every season because they change
     * I updated the numbers to reflect season IDs for the current year..
     *******/

    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int BUNDESLIGA3 = 403;
    public static final int EREDIVISIE = 404;
    public static final int CHAMPIONS_LEAGUE = 405;

    public static String getLeague(int league_num, Context context) {

        switch (league_num) {

            case BUNDESLIGA1 : return context.getString(R.string.bund_1);
            case BUNDESLIGA2 : return context.getString(R.string.bund_2);
            case BUNDESLIGA3 : return context.getString(R.string.bund_3);
            case LIGUE1 : return context.getString(R.string.ligue_1);
            case LIGUE2 : return context.getString(R.string.ligue_2);
            case PREMIER_LEAGUE : return context.getString(R.string.premier_league);
            case PRIMERA_DIVISION : return context.getString(R.string.primera_divison);
            case SEGUNDA_DIVISION : return context.getString(R.string.segunda_divison);
            case SERIE_A : return context.getString(R.string.seria_a);
            case PRIMERA_LIGA : return  context.getString(R.string.primera_liga);
            case EREDIVISIE : return context.getString(R.string.eredivisie);
            case CHAMPIONS_LEAGUE : return context.getString(R.string.champions_league);
            default: return context.getString(R.string.unknown_league);

        }
    }

    public static String getMatchDay(int match_day,int league_num, Context context) {

        if(league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.group_stage_text);
            } else if(match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout_round);
            } else if(match_day == 9 || match_day == 10) {
                return context.getString(R.string.quarter_final);
            } else if(match_day == 11 || match_day == 12) {
                return context.getString(R.string.semi_final);
            } else {
                return context.getString(R.string.final_text);
            }
        } else {
            return String.format(context.getString(R.string.matchday_text), String.valueOf(match_day));
        }
    }

    public static String getScores(int home_goals,int awaygoals) {
        if(home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname) {

        if (teamname==null){return R.drawable.no_icon;}

        // Normalize accents in string
        teamname = Normalizer
                .normalize(teamname, Normalizer.Form.NFD) // Separate the accents
                .replaceAll("[^\\p{ASCII}]", "");         // Remove accents

        switch (teamname) {
            // This is the set of icons that are currently in the app. Feel free to find and add more
            // as you go..... man that was tedious..

            case "ACF Fiorentina" : return R.drawable.acf_fiorentina;
            case "AFC Bournemouth" : return R.drawable.afc_bournemouth;
            case "Arsenal FC" : return R.drawable.arsenal;
            case "AS Roma" : return R.drawable.as_roma;
            case "Aston Villa FC" : return R.drawable.aston_villa;
            case "Bayer Leverkusen" : return R.drawable.bayer_leverkusen;
            case "Burnley FC" : return R.drawable.burnley_fc_hd_logo;
            case "Chelsea FC" : return R.drawable.chelsea;
            case "Crystal Palace FC" : return R.drawable.crystal_palace_fc;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "FC Barcelona" : return R.drawable.fc_barcelona;
            case "1. FC Kaiserslautern" : return R.drawable.fc_kaiserslautern;
            case "1. FC Nurnberg" : return R.drawable.fc_nurnberg;
            case "FC St. Pauli" : return R.drawable.fc_st_pauli;
            case "Hull City AFC" : return R.drawable.hull_city_afc_hd_logo;
            case "Leicester City FC" : return R.drawable.leicester_city_fc_hd_logo;
            case "Liverpool FC" : return R.drawable.liverpool;
            case "Manchester City FC" : return R.drawable.manchester_city;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Newcastle United FC" : return R.drawable.newcastle_united;
            case "Norwich City FC" : return R.drawable.norwich_city_fc;
            case "Queens Park Rangers FC" : return R.drawable.queens_park_rangers_hd_logo;
            case "Real Madrid CF" : return R.drawable.real_madrid_cf;
            case "Southampton FC" : return R.drawable.southampton_fc;
            case "Stoke City FC" : return R.drawable.stoke_city;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Swansea City FC" : return R.drawable.swansea_city_afc;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "Valencia CF" : return R.drawable.valencia_cf;
            case "Watford FC" : return R.drawable.watford_fc;
            case "West Bromwich Albion FC" : return R.drawable.west_bromwich_albion_hd_logo;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Fortuna Dusseldorf" : return R.drawable.fortuna_dusseldorf;
            case "Karlsruher SC" : return R.drawable.karlsruher_sc;
            case "TSV 1860 Munchen" : return R.drawable.tsv_1860_munchen;
            case "SV Sandhausen" : return R.drawable.sv_sandhausen;
            case "Granada CF" : return R.drawable.granada_cf;
            case "Sporting Gijon" : return R.drawable.sporting_gijon;
            case "Eintracht Braunschweig" : return R.drawable.eintracht_braunschweig;
            case "RCD Espanyol" : return R.drawable.rcd_espanyol;
            case "Real Betis" : return R.drawable.real_betis;
            case "Rayo Vallecano de Madrid" : return R.drawable.rayo_vallecano_de_madrid;
            case "Red Bull Leipzig" : return R.drawable.red_bull_leipzig;
            case "1. FC Heidenheim 1846" : return R.drawable.fc_heidenheim;
            case "FSV Frankfurt" : return R.drawable.fsv_frankfurt;
            case "MSV Duisburg" : return R.drawable.msv_duisburg;
            case "Arminia Bielefeld" : return R.drawable.arminia_bielefeld;
            case "SC Freiburg" : return R.drawable.sc_freiburg;
            case "Werder Bremen" : return R.drawable.werder_bremen;
            case "FC Bayern Munchen" : return R.drawable.fc_bayern_munchen;
            case "1. FSV Mainz 05" : return R.drawable.fsv_mainz_05;
            case "Bor. Monchengladbach" : return R.drawable.bor_monchengladbach;
            case "VfB Stuttgart" : return R.drawable.vfb_stuttgart;
            case "SV Darmstadt 98" : return R.drawable.sv_darmstadt_98;
            case "Borussia Dortmund" : return R.drawable.borussia_dortmund;
            case "Hertha BSC" : return R.drawable.hertha_bsc;
            case "Eintracht Frankfurt" : return R.drawable.eintracht_frankfurt;
            case "TSG 1899 Hoffenheim" : return R.drawable.tsg_1899_hoffenheim;
            case "FC Augsburg" : return R.drawable.fc_augsburg;
            case "FC Schalke 04" : return R.drawable.fc_schalke_04;
            case "Hamburger SV" : return R.drawable.hamburger_sv;
            case "Athletic Club" : return R.drawable.athletic_club;
            case "RC Deportivo La Coruna" : return R.drawable.rc_deportivo_la_coruna;
            case "RC Celta de Vigo" : return R.drawable.rc_celta_de_vigo;
            case "Villarreal CF" : return R.drawable.villarreal_cf;
            case "Sevilla FC" : return R.drawable.sevilla_fc;
            case "SD Eibar" : return R.drawable.sd_eibar;
            case "Malaga CF" : return R.drawable.malaga_cf;
            case "Levante UD" : return R.drawable.levante_ud;
            case "VfL Bochum" : return R.drawable.vfl_bochum;
            case "SpVgg Greuther Furth" : return R.drawable.spvgg_greuther_furth;
            case "1. FC Union Berlin" : return R.drawable.fc_union_berlin;
            case "SC Paderborn 07" : return R.drawable.sc_paderborn_07;
            case "FC Ingolstadt 04" : return R.drawable.fc_ingolstadt;
            case "1. FC Koln" : return R.drawable.fc_koln;
            case "Hannover 96" : return R.drawable.hannover_96;
            case "VfL Wolfsburg" : return R.drawable.vfl_wolfsburg;
            case "Club Atletico de Madrid" : return R.drawable.club_athletico_de_madrid;
            case "Real Sociedad de Futbol" : return R.drawable.real_sociedad_de_futbol;
            case "UD Las Palmas" : return R.drawable.ud_las_palmas;
            case "Getafe CF" : return R.drawable.getafe_cf;
            case "SS Lazio" : return R.drawable.ss_lazio;
            case "US Sassuolo Calcio" : return R.drawable.us_sassuolo_calcio;
            case "SSC Napoli" : return R.drawable.ssc_napoli;
            case "AC Chievo Verona" : return R.drawable.ac_chievo_verona;
            case "Hellas Verona FC" : return R.drawable.hellas_verona_fc;
            case "UC Sampdoria" : return R.drawable.uc_sampdoria;
            case "Torino FC" : return R.drawable.torino_fc;
            case "Bologna FC" : return R.drawable.bologna_fc;
            case "Carpi FC" : return R.drawable.carpi_fc;
            case "Atalanta BC" : return R.drawable.atalanta_bc;
            case "Juventus Turin" : return R.drawable.juventus_turin;
            case "Frosinone Calcio" : return R.drawable.frosinone_calcio;
            case "Udinese Calcio" : return R.drawable.udinese_calcio;
            case "Genoa CFC" : return R.drawable.genoa_cfc;
            case "Empoli FC" : return R.drawable.empoli_fc;
            case "AC Milan" : return R.drawable.ac_milan;
            case "FC Internazionale Milano" : return R.drawable.fc_internazionale_milano;
            case "US Citta di Palermo" : return R.drawable.us_citta_di_palermo;

            default: return R.drawable.no_icon;
        }
    }
}
