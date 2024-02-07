package com.buddy4life.modules.add_post

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.Navigation
import coil.load
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

    private var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { result ->
        binding.ivDogAvatar.load(result) {
            crossfade(true)
        }
    }

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

        binding.ivDogAvatar.setOnClickListener {
            launcher.launch(
                PickVisualMediaRequest.Builder().setMediaType(ImageOnly).build()
            )
        }

        binding.etDogName.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogName.error = null
            } else {
                binding.textInputLayoutDogName.error = "Required*"
            }
        }

        binding.actvDogBreed.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogBreed.error = null
            } else {
                binding.textInputLayoutDogBreed.error = "Required*"
            }
        }

        binding.etDogAge.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogAge.error = null
            } else {
                binding.textInputLayoutDogAge.error = "Required*"
            }
        }

        binding.btnAddPostCancel.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.postsFragment)
        }

        binding.btnAddPostSave.setOnClickListener {
            val name: String? = binding.etDogName.text?.toString()
            val breed: String? = binding.actvDogBreed.text?.toString()
            val ageText: String? = binding.etDogAge.text?.toString()
            val age = try {
                ageText?.toInt()
            } catch (e: NumberFormatException) {
                null
            }

            if (binding.etDogName.text.toString().isEmpty()) {
                binding.textInputLayoutDogName.error = "Required*"
            }
            if (binding.actvDogBreed.text.toString().isEmpty()) {
                binding.textInputLayoutDogBreed.error = "Required*"
            }
            if (binding.etDogAge.text.toString().isEmpty()) {
                binding.textInputLayoutDogAge.error = "Required*"
            }

//            if (!name.isNullOrEmpty() && !breed.isNullOrEmpty() && breedsNames?.contains(breed) == true && age != null) {
            if (!name.isNullOrEmpty() && !breed.isNullOrEmpty() && age != null) {
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