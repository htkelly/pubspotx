package ie.wit.pubspotx.ui.addpub

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.pubspotx.R
import ie.wit.pubspotx.databinding.FragmentAddPubBinding
import ie.wit.pubspotx.models.PubModel
import ie.wit.pubspotx.ui.auth.LoggedInViewModel

class AddPubFragment : Fragment() {

    private var _fragBinding: FragmentAddPubBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var addPubViewModel: AddPubViewModel

    // private val listPubsViewModel: ListPubsViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAddPubBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        addPubViewModel = ViewModelProvider(this).get(AddPubViewModel::class.java)
        addPubViewModel.observableStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let { render(status) }
        })
        setButtonListener(fragBinding)

        return root
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    // this wasn't working with findNavController().popBackStack() - not clear why
                    findNavController().navigate(R.id.action_addPubFragment_to_listPubsFragment)
                }
            }
            false -> Toast.makeText(context, getString(R.string.addPubError), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun setButtonListener(layout: FragmentAddPubBinding) {
        layout.addButton.setOnClickListener {
            if (layout.editPubName.text!!.isEmpty() or layout.editPubDescription.text!!.isEmpty())
                Toast.makeText(context, "Enter details!", Toast.LENGTH_LONG).show()
            else {
                addPubViewModel.addPub(
                    loggedInViewModel.liveFirebaseUser,
                    PubModel(
                        name = layout.editPubName.text.toString(),
                        description = layout.editPubDescription.text.toString(),
                        rating = layout.rating.rating.toInt(),
                        uid = loggedInViewModel.liveFirebaseUser.value?.uid!!
                    )
                )
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_add_pub, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}