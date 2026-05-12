package com.example.foyudemo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session  = SessionManager(requireContext())

        val tvName          = view.findViewById<TextView>(R.id.tvName)
        val tvEmail         = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone         = view.findViewById<TextView>(R.id.tvPhone)
        val tvRetrofitName  = view.findViewById<TextView>(R.id.tvRetrofitName)
        val tvRetrofitEmail = view.findViewById<TextView>(R.id.tvRetrofitEmail)
        val tvLoading       = view.findViewById<TextView>(R.id.tvLoading)
        val btnLogout       = view.findViewById<Button>(R.id.btnLogout)

        // Load local session data
        tvName.text  = session.getUserName()  ?: "N/A"
        tvEmail.text = session.getUserEmail() ?: "N/A"
        tvPhone.text = session.getUserPhone() ?: "N/A"

        // Retrofit dummy fetch
        RetrofitClient.instance.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                tvLoading.visibility = View.GONE
                if (response.isSuccessful) {
                    val user = response.body()
                    tvRetrofitName.text  = "API User: ${user?.name}"
                    tvRetrofitEmail.text = "API Email: ${user?.email}"
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                tvLoading.text = "API unavailable"
            }
        })

        // Logout with confirmation
        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    session.clearSession()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}