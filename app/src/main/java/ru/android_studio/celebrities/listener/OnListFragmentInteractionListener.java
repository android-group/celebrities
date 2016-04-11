package ru.android_studio.celebrities.listener;

import ru.android_studio.celebrities.model.db.ArtistDB;

/*
    * Листнер обрабатывает клик по строчке
    * */
public interface OnListFragmentInteractionListener {
    void onListFragmentInteraction(ArtistDB item);
}
