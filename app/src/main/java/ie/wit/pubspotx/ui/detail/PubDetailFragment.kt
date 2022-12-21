package ie.wit.pubspotx.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ie.wit.pubspotx.databinding.FragmentPubDetailBinding
import ie.wit.pubspotx.ui.auth.LoggedInViewModel
import ie.wit.pubspotx.ui.listpubs.ListPubsViewModel
import timber.log.Timber


class PubDetailFragment : Fragment() {

    private lateinit var detailViewModel: PubDetailViewModel
    private val args by navArgs<PubDetailFragmentArgs>()
    private var _fragBinding: FragmentPubDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val listPubsViewModel: ListPubsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentPubDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(PubDetailViewModel::class.java)
        detailViewModel.observablePub.observe(viewLifecycleOwner, Observer { render() })

        fragBinding.editPubButton.setOnClickListener {
            detailViewModel.updatePub(
                loggedInViewModel.liveFirebaseUser.value?.uid!!,
                args.pubid, fragBinding.pubvm?.observablePub!!.value!!
            )
            findNavController().navigateUp()
        }

        fragBinding.deletePubButton.setOnClickListener {
            listPubsViewModel.delete(
                loggedInViewModel.liveFirebaseUser.value?.uid!!,
                detailViewModel.observablePub.value?.uid!!
            )
            findNavController().navigateUp()
        }

        return root
    }

    private fun render() {
        //fragBinding.editMessage.setText("A Message")
        //fragBinding.editUpvotes.setText("0")
        fragBinding.pubvm = detailViewModel
        Timber.i("Retrofit fragBinding.pubvm == $fragBinding.pubvm")
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getPub(
            loggedInViewModel.liveFirebaseUser.value?.uid!!,
            args.pubid
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}