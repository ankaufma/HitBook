package htwg.de.hitbook.comparator;

import android.content.Context;
import android.content.res.Resources;

import java.util.Comparator;

import htwg.de.hitbook.HistoryActivity;
import htwg.de.hitbook.R;
import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 28.12.2014.
 */
public class TreeComparator implements Comparator<FelledTree> {

    public static final int SORT_OPTION_DATE_NEW_OLD    = 0;
    public static final int SORT_OPTION_DATE_OLD_NEW    = 1;
    public static final int SORT_OPTION_LUMBER_A_Z      = 2;
    public static final int SORT_OPTION_LUMBER_Z_A      = 3;
    public static final int SORT_OPTION_TEAM_A_Z        = 4;
    public static final int SORT_OPTION_TEAM_Z_A        = 5;

    private int compareOption;

    public TreeComparator(int compareOption){
        this.compareOption = compareOption;
    }

    @Override
    public int compare(FelledTree felledTree1, FelledTree felledTree2) {
        int result;

        switch (compareOption) {
            case SORT_OPTION_DATE_NEW_OLD:
                result = felledTree1.getDate().compareTo(felledTree2.getDate());
                break;

            case SORT_OPTION_DATE_OLD_NEW:
                result = felledTree2.getDate().compareTo(felledTree1.getDate());
                break;

            case SORT_OPTION_LUMBER_A_Z:
                result = felledTree1.getLumberjack().compareTo(felledTree2.getLumberjack());
                break;

            case SORT_OPTION_LUMBER_Z_A:
                result = felledTree2.getLumberjack().compareTo(felledTree1.getLumberjack());
                break;

            case SORT_OPTION_TEAM_A_Z:
                result = felledTree1.getTeam().compareTo(felledTree2.getTeam());
                break;

            case SORT_OPTION_TEAM_Z_A:
                result = felledTree2.getTeam().compareTo(felledTree1.getTeam());
                break;

            default:
                // Same as SORT_OPTION_DATE_NEW_OLD
                result = felledTree1.getDate().compareTo(felledTree2.getDate());
                break;

        }

        return result;
    }
}
