package ie.wit.pubspotx.ui.listpubs

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.pubspotx.R
import ie.wit.pubspotx.adapters.PubAdapter
import ie.wit.pubspotx.adapters.PubClickListener
import ie.wit.pubspotx.databinding.FragmentListPubsBinding
import ie.wit.pubspotx.models.PubModel
import ie.wit.pubspotx.ui.auth.LoggedInViewModel
import ie.wit.pubspotx.utils.*

class ListPubsFragment : Fragment(), PubClickListener {

    private var _fragBinding: FragmentListPubsBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader: AlertDialog
    private val listPubsViewModel: ListPubsViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentListPubsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ListPubsFragmentDirections.actionListPubsFragmentToAddPubFragment()
            findNavController().navigate(action)
        }
        showLoader(loader, "Downloading Pubs")
        listPubsViewModel.observablePubsList.observe(viewLifecycleOwner, Observer { pubs ->
            pubs?.let {
                render(pubs as ArrayList<PubModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader, "Deleting Pub")
                val adapter = fragBinding.recyclerView.adapter as PubAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                listPubsViewModel.delete(
                    listPubsViewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as PubModel).uid!!
                )

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onPubClick(viewHolder.itemView.tag as PubModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider,
            SearchView.OnQueryTextListener {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_list_pubs, menu)

                val item = menu.findItem(R.id.togglePubs) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val togglePubs: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                togglePubs.isChecked = false

                togglePubs.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) listPubsViewModel.loadAll()
                    else listPubsViewModel.load()
                }

                val searchItem = menu.findItem(R.id.search)
                val searchView: SearchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(this)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) listPubsViewModel.loadFiltered(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(pubsList: ArrayList<PubModel>) {
        fragBinding.recyclerView.adapter =
            PubAdapter(pubsList, this, listPubsViewModel.readOnly.value!!)
        if (pubsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.pubsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.pubsNotFound.visibility = View.GONE
        }
    }

    override fun onPubClick(pub: PubModel) {
        val action = ListPubsFragmentDirections.actionListPubsFragmentToPubDetailFragment(pub.uid!!)
        if (!listPubsViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader, "Downloading Pubs")
            if (listPubsViewModel.readOnly.value!!)
                listPubsViewModel.loadAll()
            else
                listPubsViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Pubs")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                listPubsViewModel.liveFirebaseUser.value = firebaseUser
                listPubsViewModel.load()
            }
        })
        //hideLoader(loader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}

