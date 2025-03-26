package com.weame.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class RssResponse(
    @field:Element(name = "channel")
    var channel: Channel? = null
)

@Root(name = "channel", strict = false)
data class Channel(
    @field:ElementList(inline = true, entry = "item")
    var items: List<NewsItem>? = null
)

@Root(name = "item", strict = false)
data class NewsItem(
    @field:Element(name = "title")
    var title: String = "",

    @field:Element(name = "link")
    var link: String = "",

    @field:Element(name = "description", required = false)
    var description: String? = "",

    @field:Element(name = "pubDate", required = false)
    var publishedAt: String? = "",

    @field:Element(name = "source", required = false)
    var source: String? = "Google News"
)
