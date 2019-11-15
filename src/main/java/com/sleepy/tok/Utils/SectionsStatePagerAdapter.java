package com.sleepy.tok.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/*This is NOT the Sections Pager Adapter. The difference is, with that, the number of fragments are few and
 * they're in tabs. The fragments adapted with this class are not tabbed, nor is there a definite number of them.
 * This way is more open to me adding extra fragments later.*/
public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    //list of fragments.
    public final List<Fragment> fragmentList = new ArrayList<>();

    //Has input as object, output as integer; If I have the Fragment object, I can get the Fragment number.
    private final HashMap<Fragment, Integer> hashFragments = new HashMap<>();

    //Has input as String, output as integer; If I have the Fragment name, I can get the Fragment number.
    private final HashMap<String, Integer> hashFragmentNumbers = new HashMap<>();

    //Has input as integer, output as String; If I have the Fragment number, I can get the Fragment name.
    private final HashMap<Integer, String> hashFragmentNames = new HashMap<>();

    //This is the main constructor for this class
    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName) {
        fragmentList.add(fragment);

        hashFragments.put(fragment, fragmentList.size() - 1);
        hashFragmentNames.put(fragmentList.size() - 1, fragmentName);
        hashFragmentNumbers.put(fragmentName, fragmentList.size() - 1);
    }

    public Integer getFragmentNumberFromName(String fragmentName) {
        if (hashFragmentNumbers.containsKey(fragmentName))
            return hashFragmentNumbers.get(fragmentName);
        else
            return null;
    }

    public Integer getFragmentNumberFromObject(Fragment fragment) {
        if (hashFragmentNumbers.containsKey(fragment))
            return hashFragmentNumbers.get(fragment);
        else
            return null;
    }

    public String getFragmentNameFromNumber(Integer fragmentNumber) {
        if (hashFragmentNames.containsKey(fragmentNumber))
            return hashFragmentNames.get(fragmentNumber);
        else
            return null;
    }
}