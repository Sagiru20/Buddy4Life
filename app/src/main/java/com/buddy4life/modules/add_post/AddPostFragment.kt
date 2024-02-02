package com.buddy4life.modules.add_post

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var breedsNames: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI(view: View) {
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
            binding.actvDogBreed.setAdapter(adapter)
        })

        binding.btnAddPostCancel.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.postsFragment)
        }

        binding.btnAddPostSave.setOnClickListener {
            val name = binding.etDogName.text.toString()
            val breed = binding.actvDogBreed.text.toString()
            val age: Int = binding.etDogAge.text.toString().toInt()

            if (TextUtils.isEmpty(binding.etDogName.text.toString())) {
                binding.textInputLayoutDogName.error = "Required"
            }
            if (TextUtils.isEmpty(binding.actvDogBreed.text.toString())) {
                binding.textInputLayoutDogBreed.error = "Required"
            }
            if (TextUtils.isEmpty(binding.etDogAge.text.toString())) {
                binding.textInputLayoutDogAge.error = "Required"
            }

            if (breed.isNotEmpty() && breedsNames?.contains(breed) == true) {
                val post = Post(name, breed, age)
                Model.instance.addPost(post) {
                    Navigation.findNavController(it).navigate(R.id.postsFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}