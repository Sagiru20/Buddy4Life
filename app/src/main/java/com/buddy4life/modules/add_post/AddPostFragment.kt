package com.buddy4life.modules.add_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.buddy4life.R
import com.buddy4life.model.Model
import com.buddy4life.model.Post


class AddPostFragment : Fragment() {
    private var nameTextField: EditText? = null
    private var breedTextField: EditText? = null
    private var ageTextField: EditText? = null
    private var messageTextView: TextView? = null
    private var saveButton: Button? = null
    private var cancelButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        nameTextField = view.findViewById(R.id.etDogName)
        breedTextField = view.findViewById(R.id.etDogBreed)
        ageTextField = view.findViewById(R.id.etDogAge)
        messageTextView = view.findViewById(R.id.tvAddPostSaved)
        saveButton = view.findViewById(R.id.btnAddPostSave)
        cancelButton = view.findViewById(R.id.btnAddPostCancel)
        messageTextView?.text = ""

        cancelButton?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.postsFragment)
        }

        saveButton?.setOnClickListener {
            val name = nameTextField?.text.toString()
            val breed = breedTextField?.text.toString()
            val age: Int = ageTextField?.text.toString().toInt()

            val post = Post(name, breed, age)
            Model.instance.addPost(post) {
                Navigation.findNavController(it).navigate(R.id.postsFragment)
            }
        }
    }
}