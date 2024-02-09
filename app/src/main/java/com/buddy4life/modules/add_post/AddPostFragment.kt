package com.buddy4life.modules.add_post

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
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
import com.buddy4life.model.Gender
import com.buddy4life.model.Model
import com.buddy4life.model.Post
import retrofit2.Response

private const val REQUIRED = "*required"

class AddPostFragment : Fragment() {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var breedsNames: List<String>? = null

    private var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { result ->
        binding.ivDogAvatar.load(result) {
            crossfade(true)
            placeholder(R.drawable.dog_icon)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun setupUI(view: View) {
        val apiService = RetrofitInstance.getRetrofitInstance().create(DogBreedApi::class.java)
        val responseLiveData: LiveData<Response<List<Breed>>> = liveData {
            val response = apiService.getBreeds()
            emit(response)
        }

        // Get all the dog breeds names from an external API using an HTTP request
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
                binding.textInputLayoutDogName.error = REQUIRED
            }
        }

        binding.actvDogBreed.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogBreed.error = null
            } else {
                binding.textInputLayoutDogBreed.error = REQUIRED
            }
        }

        binding.etDogAge.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogAge.error = null
            } else {
                binding.textInputLayoutDogAge.error = REQUIRED
            }
        }

        binding.etDogDescription.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                binding.textInputLayoutDogDescription.error = null
            } else {
                binding.textInputLayoutDogDescription.error = REQUIRED
            }
        }

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                binding.tvGenderRadioGroupError.visibility = View.VISIBLE
            } else {
                binding.tvGenderRadioGroupError.visibility = View.GONE
            }
        }

        binding.etDogDescription.setOnTouchListener { touchView, motionEvent ->
            if (touchView.id == binding.etDogDescription.id) {
                when (motionEvent.actionMasked) {
                    MotionEvent.ACTION_DOWN -> touchView.parent.requestDisallowInterceptTouchEvent(
                        true
                    )

                    MotionEvent.ACTION_UP -> touchView.parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
            }
            false
        }

        binding.btnAddPostCancel.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.postsFragment)
        }

        binding.btnAddPostSave.setOnClickListener {
            val name: String? = binding.etDogName.text?.toString()
            val breed: String? = binding.actvDogBreed.text?.toString()
            val description: String? = binding.etDogDescription.text?.toString()

            val rbGenderCheckedId: Int = binding.rgGender.checkedRadioButtonId
            val gender: Gender?
            if (rbGenderCheckedId != -1) {
                gender = try {
                    val checkedRadioButton: RadioButton =
                        binding.rgGender.findViewById(rbGenderCheckedId)
                    Gender.valueOf(checkedRadioButton.text.toString().uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            } else {
                gender = null
                binding.tvGenderRadioGroupError.visibility = View.VISIBLE
            }

            val ageText: String? = binding.etDogAge.text?.toString()
            val age = try {
                ageText?.toInt()
            } catch (e: NumberFormatException) {
                null
            }

//            if (!name.isNullOrEmpty() && !breed.isNullOrEmpty() && breedsNames?.contains(breed) == true && age != null) {
            if (!name.isNullOrEmpty() && !breed.isNullOrEmpty() && gender != null && age != null && !description.isNullOrEmpty()) {
                val post = Post(name, breed, gender, age, description)
                Model.instance.addPost(post) {
                    Navigation.findNavController(it).navigate(R.id.postsFragment)
                }
            } else {
                if (binding.etDogName.text.toString().isEmpty()) {
                    binding.textInputLayoutDogName.error = REQUIRED
                }
                if (binding.actvDogBreed.text.toString().isEmpty()) {
                    binding.textInputLayoutDogBreed.error = REQUIRED
                }
                if (binding.etDogAge.text.toString().isEmpty()) {
                    binding.textInputLayoutDogAge.error = REQUIRED
                }
                if (binding.etDogDescription.text.toString().isEmpty()) {
                    binding.textInputLayoutDogDescription.error = REQUIRED
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
