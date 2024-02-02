package com.buddy4life.modules.add_post

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.Navigation
import com.buddy4life.R
import com.buddy4life.databinding.FragmentAddPostBinding
import com.buddy4life.dog_breed_api.DogBreedApi
import com.buddy4life.dog_breed_api.RetrofitInstance
import com.buddy4life.model.Breed
import com.buddy4life.model.Model
import com.buddy4life.model.Post
import retrofit2.Response


class AddPostFragment : Fragment() {
    private lateinit var binding: FragmentAddPostBinding

    private var nameTextField: EditText? = null
    private var breedAutoCompleteTextField: AutoCompleteTextView? = null
    private var ageTextField: EditText? = null
    private var saveButton: Button? = null
    private var cancelButton: Button? = null

    private var breedsNames: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(view: View) {
        nameTextField = binding.etDogName
        breedAutoCompleteTextField = binding.actvDogBreed
        ageTextField = binding.etDogAge
        saveButton = binding.btnAddPostSave
        cancelButton = binding.btnAddPostCancel

        val apiService = RetrofitInstance.getRetrofitInstance().create(DogBreedApi::class.java)
        val responseLiveData: LiveData<Response<List<Breed>>> = liveData {
            val response = apiService.getBreeds()
            emit(response)
        }

        responseLiveData.observe(viewLifecycleOwner, Observer { response ->
            breedsNames = response.body()?.map { it.breedName } ?: emptyList()
            val adapter = ArrayAdapter(
                view.context, android.R.layout.simple_dropdown_item_1line, breedsNames!!
            )
            breedAutoCompleteTextField?.setAdapter(adapter)
        })

        cancelButton?.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.postsFragment)
        }

        saveButton?.setOnClickListener {
            val name = nameTextField?.text.toString()
            val breed = breedAutoCompleteTextField?.text.toString()
            val age: Int = ageTextField?.text.toString().toInt()

            if (breed.isNotEmpty() && breedsNames?.contains(breed) == true) {
                val post = Post(name, breed, age)
                Model.instance.addPost(post) {
                    Navigation.findNavController(it).navigate(R.id.postsFragment)
                }
            }
        }
    }
}