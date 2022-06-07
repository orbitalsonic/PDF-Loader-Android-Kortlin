package com.orbitalsonic.pdfloader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orbitalsonic.pdfloader.R
import com.orbitalsonic.pdfloader.databinding.ItemFilesBinding
import com.orbitalsonic.pdfloader.datamodel.FileItem
import com.orbitalsonic.pdfloader.interfaces.OnItemClickListener

class AdapterFilesLoader : ListAdapter<FileItem, RecyclerView.ViewHolder>(DATA_COMPARATOR){

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemFilesBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.item_files,parent,false)
        viewHolder = LoaderViewHolder(binding, mListener!!)
        return viewHolder

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val viewHolder = holder as LoaderViewHolder
        viewHolder.bind(currentItem)

    }


    class LoaderViewHolder(binding:ItemFilesBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        private val mBinding = binding
        init {

            binding.item.setOnClickListener {
                val mPosition = adapterPosition
                listener.onItemClick(mPosition)
            }

        }

        fun bind(mCurrentItem: FileItem) {
            mBinding.fileName.text = mCurrentItem.fileName
        }

    }

    companion object {
        private val DATA_COMPARATOR = object : DiffUtil.ItemCallback<FileItem>() {
            override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
                return oldItem.pdfFilePath == newItem.pdfFilePath
            }
        }
    }

}