package com.aptitekk.binghamapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aptitekk.binghamapp.cards.CalendarEventCard;
import com.aptitekk.binghamapp.cards.CalendarEventView;
import com.aptitekk.binghamapp.cards.MarkedCalendarEventCard;
import com.aptitekk.binghamapp.rssGoogleCalendar.CalendarDog;
import com.aptitekk.binghamapp.rssGoogleCalendar.CalendarEvent;
import com.aptitekk.binghamapp.rssnewsfeed.RSSNewsFeed;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class UpcomingEventsFragment extends Fragment implements MainActivity.FeedListener {

    private RecyclerView recyclerView;

    private CardArrayAdapter cardArrayAdapter;
    private CardListView listView;

    private CalendarDog eventsFeed;

    private boolean showABDays = true;

    public UpcomingEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).addFeedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem calendarItem = menu.add("calendar");
        calendarItem.setIcon(R.drawable.ic_calendar_grey600_48dp);
        calendarItem.getIcon().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // Set color to white
        calendarItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dialog.Builder builder;
        if (item.getTitle().toString().equalsIgnoreCase("calendar")) {
            builder = new DatePickerDialog.Builder(R.style.Material_App_Dialog_DatePicker) {
                @Override
                public void onPositiveActionClicked(DialogFragment fragment) {
                    DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                    Date date = dialog.getCalendar().getTime();
                    //recyclerView.scrollToPosition(CalendarDog.findPositionFromDate(eventsFeed.getEvents(), date));
                    listView.smoothScrollToPosition(CalendarDog.findPositionFromDate(eventsFeed.getEvents(), date));
                    Log.i(MainActivity.LOG_NAME, eventsFeed.getEvents().get(CalendarDog.findPositionFromDate(eventsFeed.getEvents(), date)).getTitle());
                    super.onPositiveActionClicked(fragment);
                }

                @Override
                public void onNegativeActionClicked(DialogFragment fragment) {
                    super.onNegativeActionClicked(fragment);
                }
            };
            builder.positiveAction("OK")
                    .negativeAction("CANCEL");
            DialogFragment fragment = DialogFragment.newInstance(builder);
            fragment.show(getChildFragmentManager(), null);
            return true;
        }
        return false;
    }

    public void populateCalendar(CalendarDog eventsFeed) {
        this.eventsFeed = eventsFeed;

        //Hide progress wheel
        getView().findViewById(R.id.progress_wheel).setVisibility(View.GONE);

        /*//Show Recycler View
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        RVAdapter adapter = new RVAdapter(eventsFeed.getEvents());
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);*/

        //populate a list full of calendar card events
        ArrayList<Card> cards = new ArrayList<Card>();
        ArrayList<CardSection> sections = new ArrayList<>();
        for (int i = 0; i < eventsFeed.getEvents().size(); i++) {
            if (eventsFeed.getEvents().get(i).getTitle().equals("A Day") || eventsFeed.getEvents().get(i).getTitle().equals("B Day")) {
                sections.add(new CardSection(i, SimpleDateFormat.getDateInstance().format(eventsFeed.getEvents().get(i).getDate().getTime())
                        + " (" + eventsFeed.getEvents().get(i).getTitle() + ")"));
                continue;
            }
            CalendarEventView card = ((eventsFeed.getEvents().get(i).getTitle().toLowerCase().contains("dance") ||
                    eventsFeed.getEvents().get(i).getTitle().toLowerCase().contains("football")) //DUMMY FILTER
                    ? new MarkedCalendarEventCard(getActivity()) : new CalendarEventCard(getActivity())); //filters will change with settings menu
            card.setTitle(eventsFeed.getEvents().get(i).getTitle());
            card.setDuration(formatDate(eventsFeed.getEvents().get(i)));
            card.setLocation(eventsFeed.getEvents().get(i).getLocation());
            //TODO: Figure out what the ID param is for
            /*ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();
            IconSupplementalAction t1 = new IconSupplementalAction(getActivity(), R.id.ic_calendar_grey600_48dp); // calendar
            t1.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {

                }
            });
            actions.add(t1);

            IconSupplementalAction t2 = new IconSupplementalAction(getActivity(), R.id.ic2); // share
            t2.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                }
            });
            actions.add(t2);
            IconSupplementalAction t3 = new IconSupplementalAction(getActivity(), R.id.ic2); // open web view
            t3.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                @Override
                public void onClick(Card card, View view) {
                }
            });
            actions.add(t3);*/
        }

        this.cardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardSection[] pointer = new CardSection[sections.size()];
        SectionedCardAdapter mAdapter = new SectionedCardAdapter(getActivity(), cardArrayAdapter);
        mAdapter.setCardSections(sections.toArray(pointer));

        this.listView = (CardListView) getActivity().findViewById(R.id.cardlist);
        if (listView != null) {
            listView.setExternalAdapter(mAdapter, cardArrayAdapter);
        }


    }

    @Override
    public void onNewsFeedDownloaded(RSSNewsFeed newsFeed) {
    }

    @Override
    public void onEventsFeedDownloaded(CalendarDog eventFeed) {
        populateCalendar(eventFeed);
    }

    private String formatDate(CalendarEvent event) {
        SimpleDateFormat headerFormat = new SimpleDateFormat("EEE hh:mmaa");
        SimpleDateFormat footerFormat = new SimpleDateFormat("hh:mmaa zzz");
        return (headerFormat.format(event.getDate().getTime()) + " - " + footerFormat.format(event.getEndTime().getTime())).replace("PM", "pm").replace("AM", "am");
    }
    /*
    @Deprecated
    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CalendarEventViewHolder> {

        public class CalendarEventViewHolder extends RecyclerView.ViewHolder {
            TextView eventDate;
            CardView card;
            TextView title;
            TextView duration;
            TextView location;
            String url = "";

            View itemView;

            CalendarEventViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                eventDate = (TextView) itemView.findViewById(R.id.eventDate);
                card = (CardView) itemView.findViewById(R.id.calendarCard);
                title = (TextView) itemView.findViewById(R.id.title);
                duration = (TextView) itemView.findViewById(R.id.duration);
                location = (TextView) itemView.findViewById(R.id.location);
            }
        }

        List<CalendarEvent> events;

        RVAdapter(List<CalendarEvent> events) {

            this.events = CalendarEvent.sort(events);
            for (int i = 0; i < events.size(); i++) {
                try {
                    events.get(i - 1);
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
                if (events.get(i - 1).getDate().get(Calendar.YEAR) == events.get(i).getDate().get(Calendar.YEAR) &&
                        events.get(i - 1).getDate().get(Calendar.MONTH) == events.get(i).getDate().get(Calendar.MONTH) &&
                        events.get(i - 1).getDate().get(Calendar.DAY_OF_MONTH) == events.get(i).getDate().get(Calendar.DAY_OF_MONTH)) {
                    events.get(i).setDateLabelVisible(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        @Override
        public CalendarEventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_event, viewGroup, false);
            return new CalendarEventViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CalendarEventViewHolder calendareventViewHolder, final int i) {
            //Refresh everything
            calendareventViewHolder.card.setVisibility(View.VISIBLE);
            calendareventViewHolder.title.setVisibility(View.VISIBLE);
            calendareventViewHolder.duration.setVisibility(View.VISIBLE);
            calendareventViewHolder.location.setVisibility(View.VISIBLE);

            if ((events.get(i).getTitle().equals("A Day") || events.get(i).getTitle().equals("B Day")) && !showABDays) {
                events.get(i).setDateLabelVisible(false);
                try {
                    if (CalendarDog.isSameDay(events.get(i), events.get(i + 1)))
                        events.get(i + 1).setDateLabelVisible(true);
                } catch (IndexOutOfBoundsException ignored) {
                }
            } else if ((events.get(i).getTitle().equals("A Day") || events.get(i).getTitle().equals("B Day")) && showABDays) {
                events.get(i).setDateLabelVisible(true);
                try {
                    if (CalendarDog.isSameDay(events.get(i), events.get(i + 1)))
                        events.get(i + 1).setDateLabelVisible(false);
                } catch (IndexOutOfBoundsException ignored) {
                }
            }

            if (!events.get(i).isDateLabelVisible()) {
                calendareventViewHolder.itemView.findViewById(R.id.eventDate).setVisibility(View.GONE);
            } else {
                calendareventViewHolder.itemView.findViewById(R.id.eventDate).setVisibility(View.VISIBLE);
                try {
                    calendareventViewHolder.eventDate.setText(SimpleDateFormat.getDateInstance().format(events.get(i).getDate().getTime()));
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            calendareventViewHolder.title.setText(events.get(i).getTitle());
            if (events.get(i).getTitle().contains("Game") || events.get(i).getTitle().contains("Football")) {
                calendareventViewHolder.title.setBackgroundColor(getResources().getColor(R.color.primary));
                calendareventViewHolder.title.setTextColor(Color.WHITE);
            } else if (events.get(i).getTitle().contains("Dance")) {
                calendareventViewHolder.title.setBackgroundColor(getResources().getColor(R.color.primary_text));
                calendareventViewHolder.title.setTextColor(Color.WHITE);
            } else {
                calendareventViewHolder.title.setBackgroundColor(Color.TRANSPARENT);
                calendareventViewHolder.title.setTextColor(Color.BLACK);
            }

            if (events.get(i).getTitle().equals("A Day") || events.get(i).getTitle().equals("B Day")) {
                if (showABDays) {
                    calendareventViewHolder.duration.setVisibility(View.GONE);
                    calendareventViewHolder.location.setVisibility(View.GONE);
                    calendareventViewHolder.url = "";
                    return;
                } else {
                    calendareventViewHolder.card.setVisibility(View.GONE);
                    calendareventViewHolder.title.setVisibility(View.GONE);
                    calendareventViewHolder.duration.setVisibility(View.GONE);
                    calendareventViewHolder.location.setVisibility(View.GONE);
                    calendareventViewHolder.url = "";
                    return;
                }
            }
            calendareventViewHolder.duration.setText(formatDate(events.get(i)));
            calendareventViewHolder.location.setText(events.get(i).getLocation());
            calendareventViewHolder.url = events.get(i).getLink();

            calendareventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(MainActivity.LOG_NAME, "Event clicked!");

                    Dialog.Builder builderChoice = new SimpleDialog.Builder(R.style.Material_App_Dialog_Simple_Light) {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);

                            //share
                            Intent textShareIntent = new Intent(Intent.ACTION_SEND);
                            textShareIntent.setType("text/plain");
                            textShareIntent.putExtra(Intent.EXTRA_TEXT, events.get(i).toString());
                            getActivity().startActivity(Intent.createChooser(textShareIntent, "Share event with..."));
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                            Dialog.Builder builder = new SimpleDialog.Builder(R.style.Material_App_Dialog_Simple_Light) {
                                @Override
                                public void onPositiveActionClicked(DialogFragment fragment) {
                                    super.onPositiveActionClicked(fragment);

                                    Intent calIntent = new Intent(Intent.ACTION_EDIT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, events.get(i).getTitle());
                                    calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, events.get(i).getLocation());
                                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                            events.get(i).getDate().getTimeInMillis());
                                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                            events.get(i).getEndTime().getTimeInMillis());
                                    getActivity().startActivity(calIntent);

                                }


                                @Override
                                public void onNegativeActionClicked(DialogFragment fragment) {
                                    super.onNegativeActionClicked(fragment);
                                }
                            };


                            ((SimpleDialog.Builder) builder).message(events.get(i).getTitle())
                                    .title("Add event to your calendar?")
                                    .positiveAction("YES")
                                    .negativeAction("NO");
                            DialogFragment dialogFragment = DialogFragment.newInstance(builder);
                            dialogFragment.show(getFragmentManager(), null);
                        }
                    };


                    ((SimpleDialog.Builder) builderChoice).message("Do you want to")
                            .positiveAction("SHARE")
                            .negativeAction("CALENDAR    or");
                    DialogFragment dialogFragment = DialogFragment.newInstance(builderChoice);
                    dialogFragment.show(getFragmentManager(), null);

                }
            });
        }

    }*/
}
