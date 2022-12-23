# PubspotX

A new version of Pubspot, this time implementing an MVVM design pattern and multi-user functionality.

## Functionality

On launching the app, users can sign up or log in with a Google account or an an email or password. This is implemented using Firebase authenticaton.

After signing in, users can navigate the app using a nav drawer. From the nav drawer they can sign out, change the app theme, and navigate to fragments for adding pubs, listing pubs, and displaying app information. They can also tap their profile picture to change it.

The add pubs fragment facilitates adding pubs with a rating, name and description (TextInputLayout and RatingBar elements are used for this).

The list pubs fragment displays a list of pubs. This defaults to a list of the pubs added by the logged in user, and can be toggled to show a list of all pubs including those added by other users. Either set of results can be filtered using a SearchView in the toolbar. When in edit mode, the user can swipe left on a pub to delete it, or swipe right to launch the detail fragment, where they can view and edit the pub's name, rating and description.

The about fragment displays some information about the app.

User profile images are persisted in Firebase cloud storage. Pub data is persisted in Firebase Realtime Database. User preferences (i.e. app theme setting) are persisted in a local SQLite database using the Room persistence library.

## UML Diagram

![][uml]

## UX / DX Approach Adopted

The app uses an MVVM design pattern with fragment based navigation. This was developed by using the DonationX case study as the initial codebase and building the app out from there.

For UX, a nav drawer is implemented for navigating between fragments, as well as signing out, changing the user's profile picture, and changing the app theme.

The user's theme preference is persisted locally using the Room persistence library, as persisting this data is nice for UX but it is not data that should necessarily be stored in the cloud.

## Personal Statement

My Pubspot app for assignment one was built by using Placemark as the starting point and adapting it. Similarly, this app uses DonationX as the starting point, and adapts it into a new version of Pubspot, PubspotX. I choose to do this in order to leverage DonationX's nav drawer and MVVM design pattern. All of the DonationX-derived components have been adapted and changed.

I would have liked to include pub location data, as in the original implementation of Pubspot, and started work on this in the feature/lcation branch of this repo, however I have not had adequate time to refactor the map and location functionality from the first assignment into the MVVM architecture of PubspotX.

As far as my achievements on this assignment go, I am most pleased by the implementation of a dark theme and local persistence of user preferences. The Room persistence features from the Placemark case study were adapted into this app to achieve this.

## References

This project adapted the DonationX case study as its initial codebase, and took some elements from the Placemark case study. Additionally, the following resources were consulted:

* https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow
* https://www.digitalocean.com/community/tutorials/android-textinputlayout-example
* https://www.section.io/engineering-education/how-is-textinputlayout-different-from-edittext/
* https://stackoverflow.com/questions/47410285/edittext-with-textinputlayout-layout-with-hint-and-border
* https://m2.material.io/develop/android/components/text-input-layout/
* https://stackoverflow.com/questions/40866229/keep-textinputlayout-hint-always-top
* https://www.codingdemos.com/android-textinputlayout-character-count/
* https://developer.android.com/develop/ui/views/theming/darktheme
* https://stackoverflow.com/questions/27378981/how-to-use-searchview-in-toolbar-android
* https://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
* https://stackoverflow.com/questions/47303819/kotlin-how-to-get-searchview-submit
* https://stackoverflow.com/questions/44098709/how-can-i-filter-an-arraylist-in-kotlin-so-i-only-have-elements-which-match-my-c
* https://developer.android.com/develop/ui/views/components/dialogs

[uml]:./public/PubspotX_UML.png