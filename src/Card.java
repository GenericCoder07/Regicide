

public class Card implements Comparable<Card>
{
	public static enum Suit
	{
		NONE, SPADES, HEARTS, CLUBS, DIAMONDS;
	}

	public static enum Rank
	{
		JOKER, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;
	}

	private Rank rank;
	private Suit suit;
	private boolean faceUp = false;
	
	public Card(Rank rank, Suit suit)
	{
		this.rank = rank;
		this.suit = suit;
		faceUp = false;
	}
	
	public void flip()
	{
		faceUp = !faceUp;
	}
	
	public void setIsFaceUp(boolean faceUp)
	{
		this.faceUp = faceUp;
	}
	
	public boolean isFaceUp()
	{
		return faceUp;
	}
	
	public final Rank getRank()
	{
		return rank;
	}
	
	public final void setRank(Rank rank)
	{
		this.rank = rank;
	}
	
	public final Suit getSuit()
	{
		return suit;
	}
	
	public final void setSuit(Suit suit)
	{
		this.suit = suit;
	}

	public int compareTo(Card o)
	{
		if(o.suit == this.suit)
		{
			if(o.rank == this.rank)
				return 0;
			
			if(o.rank.ordinal() < this.rank.ordinal())
				return 1;
			
			return -1;
		}
		
		if(o.suit.ordinal() < this.suit.ordinal())
			return 1;
		
		return -1;
	}
	
}
