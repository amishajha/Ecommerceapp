package com.example.shopnest.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopnest.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment :BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        return root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashoard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onResume() {
        super.onResume()

        getDashboardItemsList()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                // TODO Step 9: Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, SettingActivity::class.java))
                // END
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, Cartlistactivity::class.java))
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    private fun getDashboardItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.pleasewait))

        Firestoreclass().getDashboardItemsList(this@DashboardFragment)
    }


    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {

        // Hide the progress dialog.
        hideProgressDialog()

        if (dashboardItemsList.size > 0) {

            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = GridLayoutManager(activity, 2)
            rv_dashboard_items.setHasFixedSize(true)

            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapter
            adapter.setOnClickListener(object :
                    DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {

                    // TODO Step 7: Launch the product details screen from the dashboard.
                    // START
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    startActivity(intent)
                    // END
                }
            })
        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }
    // END
}




