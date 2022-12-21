package ie.wit.pubspotx.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.wit.pubspotx.databinding.CardPubBinding
import ie.wit.pubspotx.models.PubModel


interface PubClickListener {
    fun onPubClick(pub: PubModel)
}

class PubAdapter constructor(
    private var pubs: ArrayList<PubModel>,
    private val listener: PubClickListener,
    private val readOnly: Boolean
) : RecyclerView.Adapter<PubAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPubBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val pub = pubs[holder.adapterPosition]
        holder.bind(pub, listener)
    }

    fun removeAt(position: Int) {
        pubs.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = pubs.size

    inner class MainHolder(val binding: CardPubBinding, private val readOnly: Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(pub: PubModel, listener: PubClickListener) {
            binding.root.tag = pub
            binding.pub = pub
            binding.root.setOnClickListener { listener.onPubClick(pub) }
            binding.executePendingBindings()
        }
    }
}