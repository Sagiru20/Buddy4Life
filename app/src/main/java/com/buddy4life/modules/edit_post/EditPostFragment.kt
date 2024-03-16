package com.buddy4life.modules.edit_post

import android.R
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.navigation.Navigation
import coil.load
import com.buddy4life.databinding.FragmentEditPostBinding
import com.buddy4life.dog_breed_api.DogBreedApi
import com.buddy4life.dog_breed_api.RetrofitInstance
import com.buddy4life.model.Breed
import com.buddy4life.model.Post.Category
import com.buddy4life.model.Post.Gender
import com.buddy4life.model.Post.Post
import com.buddy4life.model.Post.PostModel
import com.buddy4life.modules.post.PostFragmentArgs
import com.squareup.picasso.Picasso
import retrofit2.Response

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private var post: Post? = null
    private var imageUri: String? = null
    private var isPostImageChanged = false
    private var breedsNames: List<String>? = null

    private var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {

            binding.ivDogImage.load(uri) {
                crossfade(true)
            }

            imageUri = uri?.toString()
            isPostImageChanged = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)

        // Get the post from DB by the given id
        val postId: String = PostFragmentArgs.fromBundle(requireArguments()).postId
        PostModel.instance.getPost(postId) { post ->
            this.post = post
            setupUI(binding.root)
        }

        return binding.root
    }

    private fun setupUI(view: View) {


        post?.let {

            val action = EditPostFragmentDirections.actionEditPostFragmentToPostFragment(post!!.id)
            val apiService = RetrofitInstance.getRetrofitInstance().create(DogBreedApi::class.java)
            val responseLiveData: LiveData<Response<List<Breed>>> = liveData {
                val response = apiService.getBreeds()
                emit(response)
            }

            // Get all the dog breeds names from an external API using an HTTP request
            responseLiveData.observe(viewLifecycleOwner, Observer { response ->
                breedsNames = response.body()?.map { it.breedName } ?: emptyList()
                val breedsAdapter = ArrayAdapter(
                    view.context, R.layout.simple_dropdown_item_1line, breedsNames!!
                )
                binding.actvDogInfoBreed.setAdapter(breedsAdapter)
            })


            val genders = listOf("Male", "Female")

            val gendersAdapter = ArrayAdapter(
                view.context, R.layout.simple_dropdown_item_1line, genders
            )
            binding.spnrDogInfoGender.setAdapter(gendersAdapter)
            val currentGenderIndex = genders.indexOf(post?.gender?.toString())
            binding.spnrDogInfoGender.setSelection(currentGenderIndex)



            binding.tvDogName.text = post?.name
            binding.tvDogBreed.text = post?.breed
            binding.tvDogGender.text = post?.gender?.toString()
            binding.tvDogAge.text = post?.age.toString()


            PostModel.instance.getPostDogImageUri(post?.id) { uri ->
                uri?.let {
                    Log.i("TAG", "Setting image from uri: $uri")
                    Picasso.get().load(uri).into(binding.ivDogImage)

                }

            }


            binding.btnCancel.setOnClickListener {

                Navigation.findNavController(it)
                    .navigate(action)

            }



            binding.ivDogImage.setOnClickListener {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
                )
            }

            binding.tvDogDescription.setText(post?.description.toString())
            binding.tvDogInfoName.setText(post?.name)
            binding.actvDogInfoBreed.setText(post?.breed)
            binding.tvDogInfoAge.setText(post?.age.toString())
            binding.tvDogInfoWeight.setText(if (post?.weight?.toString() != null) post?.weight?.toString() else "0")
            binding.tvDogInfoHeight.setText((if (post?.height?.toString() != null) post?.height?.toString() else "0"))

            if (imageUri == null) {

                imageUri = post!!.dogImageUri

            }

            binding.btnSavePost.setOnClickListener {

                if (!binding.tvDogInfoName.text.toString()
                        .isEmpty() && !binding.actvDogInfoBreed.text.toString()
                        .isEmpty() && !binding.tvDogInfoAge.text.toString()
                        .isEmpty() && !binding.tvDogDescription.text.toString().isEmpty() &&
                    breedsNames?.contains(binding.actvDogInfoBreed.text.toString()) == true
                ) {

                    val newPost = Post(
                        post!!.id,
                        binding.tvDogInfoName.text.toString(),
                        binding.actvDogInfoBreed.text.toString(),
                        Gender.valueOf(
                            binding.spnrDogInfoGender.selectedItem.toString().uppercase()
                        ),
                        binding.tvDogInfoAge.text.toString().toInt(),
                        binding.tvDogDescription.text.toString(),
                        imageUri,
                        Category.ADOPTION_REQUEST,
                        binding.tvDogInfoWeight.text.toString().toInt(),
                        binding.tvDogInfoHeight.text.toString().toInt(),
                        post!!.createdTime,
                        post!!.lastUpdated,
                        post?.ownerId
                    )


                    PostModel.instance.updatePost(newPost) { isPostUpdated ->

                        if (isPostUpdated) {

                            if (isPostImageChanged) {

                                newPost.dogImageUri?.let {

                                    PostModel.instance.setPostDogImage(
                                        newPost.id,
                                        newPost.dogImageUri
                                    ) { isPostImageSaved ->

                                        if (isPostImageSaved) {

                                            Toast.makeText(
                                                requireContext(),
                                                "Post Updated Successfully",
                                                Toast.LENGTH_SHORT,
                                            ).show()

                                        } else {

                                            Toast.makeText(
                                                requireContext(),
                                                "Failed to update post's image",
                                                Toast.LENGTH_SHORT,
                                            ).show()

                                        }

                                    }

                                }


                            } else {

                                Toast.makeText(
                                    requireContext(),
                                    "Post Updated Successfully",
                                    Toast.LENGTH_SHORT,
                                ).show()

                            }


                        } else {

                            Toast.makeText(
                                requireContext(),
                                "Failed to update post",
                                Toast.LENGTH_SHORT,
                            ).show()

                        }


                    }

                    Navigation.findNavController(it)
                        .navigate(action)

                } else {

                    if (breedsNames?.contains(binding.actvDogInfoBreed.text.toString()) == false) {

                        Toast.makeText(
                            requireContext(),
                            "breed must be in the list",
                            Toast.LENGTH_SHORT,
                        ).show()

                    } else {

                        Toast.makeText(
                            requireContext(),
                            "required parameter can't be empty",
                            Toast.LENGTH_SHORT,
                        ).show()


                    }

                }

            }


        }


    }

}