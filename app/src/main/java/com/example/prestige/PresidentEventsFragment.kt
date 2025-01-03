package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PresidentEventsFragment : Fragment() {

    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private val eventsList = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_president_events, container, false)

        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = EventsAdapter(eventsList, true) { eventId ->
            deleteEvent(eventId)
        }
        eventsRecyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventsList.add(event)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val scheduleButton = view.findViewById<Button>(R.id.scheduleButton)
        scheduleButton.setOnClickListener {
            val fragment = ScheduleEventFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.president_fragment_container, fragment) // Replace with your container ID
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun deleteEvent(eventId: String) {
        databaseReference.child(eventId).removeValue().addOnSuccessListener {
            Toast.makeText(requireContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to delete event", Toast.LENGTH_SHORT).show()
        }
    }
}