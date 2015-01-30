// Copyright 2014 Grzegorz Wlodarek
// Distributed under the terms of the GNU General Public License.
//
// This file is part of Toronto Transit.
//
// This is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This file is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this file.  If not, see <http://www.gnu.org/licenses/>.

//Package
package com.gregorywlodarek.torontotransit.torontotransit;

//Imports
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Holds all the tabs that the user can swipe to horizontally.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter
{

    //Instance variables
    private Favourites fav = new Favourites();
    private Find find = new Find();
    private Info info = new Info();
    private Nearby nearby = new Nearby();
    private FavouritesData fd = FavouritesData.createFavouritesData();

    /**
     * Default Constructor.
     *
     * @param fm Fragment Manager.
     */
    public TabPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    /**
     * Returns the Fragment at the specified position (i).
     *
     * @param i The fragment spot.
     * @return the fragment at given i.
     */
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                //Subscribe this tab to FavouritesData to update the Favourites tab whenever
                //FavouritesData changes its state.
                fd.subscribe(fav);
                return fav;
            case 1:
                return find;
            case 2:
                return nearby;
            case 3:
                return info;
        }

        return null;

    }

    /**
     * Contains the number of fragments in the application.
     *
     * @return the number of fragments.
     */
    public int getCount()
    {
        return 4;
    }

}
